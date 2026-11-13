package com.example.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.model.TaskItem
import com.example.data.model.TaskStatus
import com.example.ui.components.formatDateDisplay
import com.example.ui.components.formatElapsedSpent
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PdfReportSection(
  val heading: String,
  val accentColor: Int,
  val entries: List<PdfReportEntry>
)

data class PdfReportEntry(
  val title: String,
  val metaLine: String,
  val bodyLines: List<String>
)

data class PdfReportModel(
  val generatedAtText: String,
  val summaryLine: String,
  val sections: List<PdfReportSection>
)

object TaskPdfExporter {

  private const val PAGE_WIDTH = 595
  private const val PAGE_HEIGHT = 842
  private const val MARGIN_X = 40f
  private const val CONTENT_WIDTH = PAGE_WIDTH - 80f
  private const val TOP_Y = 48f
  private const val BOTTOM_LIMIT = 786f

  fun buildReportModel(
    tasks: List<TaskItem>,
    nowMillis: Long = System.currentTimeMillis()
  ): PdfReportModel {
    val timestamp = SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault()).format(Date(nowMillis))
    val todoTasks = tasks.filter { it.status == TaskStatus.TODO }
    val inProgressTasks = tasks.filter { it.status == TaskStatus.IN_PROGRESS }
    val completedTasks = tasks.filter { it.status == TaskStatus.COMPLETED }

    val summaryLine =
      "Total Tasks: ${tasks.size}  |  To Do: ${todoTasks.size}  |  In Progress: ${inProgressTasks.size}  |  Completed: ${completedTasks.size}"

    fun buildTaskSection(title: String, list: List<TaskItem>, color: Int): PdfReportSection? {
      if (list.isEmpty()) return null
      val entries = list.mapIndexed { idx, task ->
        val metaParts = mutableListOf(
          "Priority: ${task.priority.displayName}",
          "Category: ${task.category}"
        )
        if (!task.dueDate.isNullOrBlank()) {
          metaParts.add("Due: ${formatDateDisplay(task.dueDate)}")
        }
        val spent = task.currentElapsedSeconds(nowMillis)
        if (spent > 0) {
          metaParts.add("Time: ${formatElapsedSpent(spent)}")
        }
        if (task.completedAt != null && task.status == TaskStatus.COMPLETED) {
          val compStr = SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault()).format(Date(task.completedAt))
          metaParts.add("Done: $compStr")
        }

        val bodyLines = mutableListOf<String>()
        if (task.description.isNotBlank()) {
          bodyLines.add(task.description)
        }
        task.subtasks.forEach { sub ->
          val mark = if (sub.isCompleted) "[✓]" else "[ ]"
          bodyLines.add("   $mark ${sub.text}")
        }

        PdfReportEntry(
          title = "${idx + 1}. ${task.title}",
          metaLine = metaParts.joinToString("  •  "),
          bodyLines = bodyLines
        )
      }
      return PdfReportSection(heading = "$title (${list.size})", accentColor = color, entries = entries)
    }

    val sections = mutableListOf<PdfReportSection>()
    buildTaskSection("In Progress Tasks", inProgressTasks, Color.rgb(217, 119, 6))?.let { sections.add(it) }
    buildTaskSection("To Do Tasks", todoTasks, Color.rgb(22, 119, 255))?.let { sections.add(it) }
    buildTaskSection("Completed Tasks", completedTasks, Color.rgb(16, 185, 129))?.let { sections.add(it) }

    return PdfReportModel(
      generatedAtText = "Generated on $timestamp",
      summaryLine = summaryLine,
      sections = sections
    )
  }

  fun exportToPdf(
    tasks: List<TaskItem>,
    outputStream: OutputStream
  ) {
    val report = buildReportModel(tasks = tasks)
    val pdf = PdfDocument()
    try {
      var pageNumber = 1
      var currentPage = pdf.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
      var canvas: Canvas = currentPage.canvas
      var y = TOP_Y

      val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(16, 42, 86)
        textSize = 19f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      }
      val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(100, 116, 139)
        textSize = 10.5f
      }
      val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(22, 119, 255)
        textSize = 13.5f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      }
      val itemTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(15, 23, 42)
        textSize = 12f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
      }
      val metaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(71, 85, 105)
        textSize = 9.5f
      }
      val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(51, 65, 85)
        textSize = 10.5f
      }
      val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(203, 213, 225)
        strokeWidth = 1f
      }
      val headerBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(227, 239, 255)
      }
      val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(148, 163, 184)
        textSize = 9.5f
      }

      fun drawFooter(targetCanvas: Canvas, pageNum: Int) {
        targetCanvas.drawLine(MARGIN_X, PAGE_HEIGHT - 36f, PAGE_WIDTH - MARGIN_X, PAGE_HEIGHT - 36f, dividerPaint)
        targetCanvas.drawText("TaskEasy Export Report", MARGIN_X, PAGE_HEIGHT - 20f, footerPaint)
        val pageLabel = "Page $pageNum"
        val labelWidth = footerPaint.measureText(pageLabel)
        targetCanvas.drawText(pageLabel, PAGE_WIDTH - MARGIN_X - labelWidth, PAGE_HEIGHT - 20f, footerPaint)
      }

      fun ensureSpace(neededHeight: Float) {
        if (y + neededHeight > BOTTOM_LIMIT) {
          drawFooter(canvas, pageNumber)
          pdf.finishPage(currentPage)
          pageNumber += 1
          currentPage = pdf.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
          canvas = currentPage.canvas
          y = TOP_Y
        }
      }

      fun drawWrappedText(
        text: String,
        x: Float,
        maxWidth: Float,
        paint: Paint,
        lineHeight: Float
      ) {
        val paragraphs = text.split("\n")
        for (paragraph in paragraphs) {
          if (paragraph.isBlank()) {
            ensureSpace(lineHeight)
            y += lineHeight * 0.6f
            continue
          }
          val words = paragraph.trim().split(Regex("\\s+"))
          var line = StringBuilder()
          for (word in words) {
            val candidate = if (line.isEmpty()) word else "$line $word"
            if (paint.measureText(candidate) <= maxWidth) {
              line = StringBuilder(candidate)
            } else {
              if (line.isNotEmpty()) {
                ensureSpace(lineHeight)
                canvas.drawText(line.toString(), x, y, paint)
                y += lineHeight
              }
              if (paint.measureText(word) > maxWidth) {
                var chunk = word
                while (chunk.isNotEmpty()) {
                  val count = paint.breakText(chunk, true, maxWidth, null).coerceAtLeast(1)
                  ensureSpace(lineHeight)
                  canvas.drawText(chunk.substring(0, count), x, y, paint)
                  y += lineHeight
                  chunk = chunk.substring(count)
                }
                line = StringBuilder()
              } else {
                line = StringBuilder(word)
              }
            }
          }
          if (line.isNotEmpty()) {
            ensureSpace(lineHeight)
            canvas.drawText(line.toString(), x, y, paint)
            y += lineHeight
          }
        }
      }

      // Header Banner
      canvas.drawRoundRect(MARGIN_X, y - 16f, PAGE_WIDTH - MARGIN_X, y + 52f, 10f, 10f, headerBgPaint)
      canvas.drawText("TaskEasy — Tasks Report", MARGIN_X + 14f, y + 7f, titlePaint)
      canvas.drawText(report.generatedAtText, MARGIN_X + 14f, y + 25f, subtitlePaint)
      canvas.drawText(report.summaryLine, MARGIN_X + 14f, y + 41f, metaPaint)
      y += 72f

      if (report.sections.isEmpty()) {
        ensureSpace(30f)
        canvas.drawText("No tasks saved yet.", MARGIN_X, y, bodyPaint)
        y += 20f
      } else {
        for (section in report.sections) {
          ensureSpace(34f)
          sectionPaint.color = section.accentColor
          canvas.drawText(section.heading, MARGIN_X, y, sectionPaint)
          y += 6f
          canvas.drawLine(MARGIN_X, y, PAGE_WIDTH - MARGIN_X, y, dividerPaint)
          y += 14f

          for (entry in section.entries) {
            ensureSpace(40f)
            drawWrappedText(entry.title, MARGIN_X, CONTENT_WIDTH, itemTitlePaint, 15f)
            drawWrappedText(entry.metaLine, MARGIN_X + 10f, CONTENT_WIDTH - 10f, metaPaint, 13f)
            for (bodyLine in entry.bodyLines) {
              drawWrappedText(bodyLine, MARGIN_X + 10f, CONTENT_WIDTH - 10f, bodyPaint, 13.5f)
            }
            y += 8f
          }
          y += 8f
        }
      }

      drawFooter(canvas, pageNumber)
      pdf.finishPage(currentPage)
      pdf.writeTo(outputStream)
      outputStream.flush()
    } finally {
      pdf.close()
    }
  }
}
