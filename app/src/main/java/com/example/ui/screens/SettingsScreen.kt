package com.example.ui.screens

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.BuildConfig
import com.example.ui.components.ManageCategoriesDialog
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.DarkNavyText
import com.example.ui.theme.InProgressOrange
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextDarkPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
  themeMode: AppThemeMode,
  taskCount: Int = 0,
  categories: List<String> = emptyList(),
  categoryTaskCounts: Map<String, Int> = emptyMap(),
  onThemeChange: (AppThemeMode) -> Unit,
  onAddCategory: (String) -> Result<String> = { Result.failure(UnsupportedOperationException()) },
  onRenameCategory: (String, String) -> Result<String> = { _, _ -> Result.failure(UnsupportedOperationException()) },
  onDeleteCategory: (String) -> Result<String> = { Result.failure(UnsupportedOperationException()) },
  onExportPdfToStream: suspend (OutputStream) -> Unit,
  onClearAllData: () -> Unit,
  onShowMessage: (String) -> Unit = {},
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val isDark = LocalIsDarkTheme.current

  var showThemeDialog by remember { mutableStateOf(false) }
  var showManageCategoriesDialog by remember { mutableStateOf(false) }
  var showExportPdfDialog by remember { mutableStateOf(false) }
  var exportStatusError by remember { mutableStateOf<String?>(null) }
  var isExportingPdf by remember { mutableStateOf(false) }
  var showClearConfirmDialog by remember { mutableStateOf(false) }
  var showAboutDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }
  var showRateDialog by remember { mutableStateOf(false) }

  val savePdfLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.CreateDocument("application/pdf")
  ) { uri ->
    if (uri != null) {
      coroutineScope.launch {
        isExportingPdf = true
        exportStatusError = null
        try {
          withContext(Dispatchers.IO) {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
              onExportPdfToStream(stream)
            } ?: throw IllegalStateException("Could not open destination file")
          }
          showExportPdfDialog = false
          onShowMessage("PDF report saved successfully")
        } catch (e: Exception) {
          exportStatusError = "Save PDF failed: ${e.localizedMessage ?: "Unknown error"}"
        } finally {
          isExportingPdf = false
        }
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(if (isDark) SurfaceDark else SurfaceLight)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back_button")) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = if (isDark) TextDarkPrimary else DarkNavyText
        )
      }

      Text(
        text = "Settings",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 19.sp
        ),
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // 1. Appearance Group
      SettingsGroupCard(title = "Appearance") {
        SettingsRowItem(
          icon = Icons.Default.WbSunny,
          iconTint = InProgressOrange,
          title = "Theme",
          value = when (themeMode) {
            AppThemeMode.SYSTEM -> "System Default"
            AppThemeMode.LIGHT -> "Light"
            AppThemeMode.DARK -> "Dark"
          },
          onClick = { showThemeDialog = true },
          testTag = "settings_theme_row"
        )
        SettingsRowItem(
          icon = Icons.Default.Language,
          iconTint = PrimaryBlue,
          title = "Language",
          value = "English",
          onClick = {},
          enabled = false,
          showArrow = false,
          testTag = "settings_language_row"
        )
      }

      // 2. Task Organization (Categories)
      SettingsGroupCard(title = "Task Organization") {
        SettingsRowItem(
          icon = Icons.AutoMirrored.Filled.Label,
          iconTint = PrimaryBlue,
          title = "Manage Categories",
          subtitle = "Add, rename, or delete task categories",
          value = "${categories.size}",
          onClick = { showManageCategoriesDialog = true },
          testTag = "settings_manage_categories_row"
        )
      }

      // 3. Data Management Group (Export in PDF + Clear All Data ONLY)
      SettingsGroupCard(title = "Data Management") {
        SettingsRowItem(
          icon = Icons.Default.PictureAsPdf,
          iconTint = PrimaryBlue,
          title = "Export Data (PDF)",
          subtitle = "Save or share your tasks as a PDF file",
          onClick = {
            exportStatusError = null
            showExportPdfDialog = true
          },
          showArrow = false,
          testTag = "settings_export_pdf_row"
        )
        SettingsRowItem(
          icon = Icons.Default.Delete,
          iconTint = PriorityHigh,
          title = "Clear All Data",
          subtitle = "Delete all tasks and reset app",
          onClick = { showClearConfirmDialog = true },
          showArrow = false,
          testTag = "settings_clear_all_row"
        )
      }

      // 3. Support Group
      SettingsGroupCard(title = "Support") {
        SettingsRowItem(
          icon = Icons.Default.Star,
          iconTint = Color(0xFFFFB300),
          title = "Rate TaskEasy",
          onClick = { showRateDialog = true },
          testTag = "settings_rate_row"
        )
        SettingsRowItem(
          icon = Icons.Default.PrivacyTip,
          iconTint = PrimaryBlue,
          title = "Privacy Policy",
          onClick = { showPrivacyDialog = true },
          testTag = "settings_privacy_row"
        )
        SettingsRowItem(
          icon = Icons.Default.Info,
          iconTint = PrimaryBlue,
          title = "About TaskEasy",
          onClick = { showAboutDialog = true },
          testTag = "settings_about_row"
        )
      }

      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Theme Dialog
  if (showThemeDialog) {
    AlertDialog(
      onDismissRequest = { showThemeDialog = false },
      title = { Text("Choose Theme", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          AppThemeMode.entries.forEach { mode ->
            val label = when (mode) {
              AppThemeMode.SYSTEM -> "System Default"
              AppThemeMode.LIGHT -> "Light"
              AppThemeMode.DARK -> "Dark"
            }
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                  onThemeChange(mode)
                  showThemeDialog = false
                }
                .padding(vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = themeMode == mode,
                onClick = {
                  onThemeChange(mode)
                  showThemeDialog = false
                },
                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = label, fontSize = 15.sp)
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showThemeDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  if (showRateDialog) {
    AlertDialog(
      onDismissRequest = { showRateDialog = false },
      title = { Text("Rate TaskEasy", fontWeight = FontWeight.Bold) },
      text = {
        Text("Thank you for using TaskEasy! Enjoying your experience? Keep planning and achieving your daily goals.")
      },
      confirmButton = {
        Button(
          onClick = { showRateDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Text("OK")
        }
      }
    )
  }

  if (showExportPdfDialog) {
    AlertDialog(
      onDismissRequest = { if (!isExportingPdf) showExportPdfDialog = false },
      title = { Text("Export as PDF", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = "Includes $taskCount ${if (taskCount == 1) "task" else "tasks"} formatted into a printable PDF report.",
            fontSize = 13.5.sp,
            color = if (isDark) Color(0xFFCBD5E1) else SlateGray
          )

          if (exportStatusError != null) {
            Text(
              text = exportStatusError!!,
              color = PriorityHigh,
              fontSize = 12.sp
            )
          }

          Button(
            onClick = {
              exportStatusError = null
              savePdfLauncher.launch(createPdfFileName())
            },
            enabled = !isExportingPdf,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("export_save_pdf_button"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
          ) {
            Icon(
              imageVector = Icons.Default.SaveAlt,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isExportingPdf) "Creating PDF..." else "Save PDF File",
              fontWeight = FontWeight.SemiBold
            )
          }

          OutlinedButton(
            onClick = {
              exportStatusError = null
              coroutineScope.launch {
                isExportingPdf = true
                try {
                  val fileUri = withContext(Dispatchers.IO) {
                    val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
                    exportDir.listFiles()?.forEach { it.delete() }
                    val outFile = File(exportDir, createPdfFileName())
                    outFile.outputStream().use { stream ->
                      onExportPdfToStream(stream)
                    }
                    FileProvider.getUriForFile(
                      context,
                      "${context.packageName}.fileprovider",
                      outFile
                    )
                  }
                  val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, fileUri)
                    putExtra(Intent.EXTRA_SUBJECT, "TaskEasy Report")
                    clipData = ClipData.newRawUri("TaskEasy Report", fileUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                  }
                  context.startActivity(Intent.createChooser(sendIntent, "Share TaskEasy PDF"))
                  showExportPdfDialog = false
                } catch (_: ActivityNotFoundException) {
                  exportStatusError = "No app found on device to share PDF files. Use 'Save PDF File' instead."
                } catch (e: Exception) {
                  exportStatusError = "Unable to share PDF: ${e.localizedMessage ?: "Error"}"
                } finally {
                  isExportingPdf = false
                }
              }
            },
            enabled = !isExportingPdf,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("export_share_pdf_button"),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = null,
              modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share PDF", fontWeight = FontWeight.SemiBold)
          }
        }
      },
      confirmButton = {},
      dismissButton = {
        TextButton(
          onClick = { showExportPdfDialog = false },
          enabled = !isExportingPdf
        ) {
          Text("Close")
        }
      }
    )
  }

  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      title = { Text("Clear All Data?", fontWeight = FontWeight.Bold, color = PriorityHigh) },
      text = {
        Text("Are you sure you want to delete all tasks? This will reset the app.")
      },
      confirmButton = {
        Button(
          onClick = {
            onClearAllData()
            showClearConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = PriorityHigh)
        ) {
          Text("Delete All")
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  if (showAboutDialog) {
    AlertDialog(
      onDismissRequest = { showAboutDialog = false },
      title = { Text("About TaskEasy", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("TaskEasy – Personal Task Manager", fontWeight = FontWeight.Bold, color = PrimaryBlue)
          Text("Plan • Focus • Complete\n\nA simple, fast, offline-first personal task manager that helps you plan, start, and complete daily tasks.", fontSize = 13.sp)
          Text("Version ${BuildConfig.VERSION_NAME}", fontSize = 12.sp, color = SlateGray)
        }
      },
      confirmButton = {
        TextButton(onClick = { showAboutDialog = false }) {
          Text("OK")
        }
      }
    )
  }

  if (showPrivacyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("100% Offline & Private", fontWeight = FontWeight.Bold, color = CompletedGreen)
          Text("TaskEasy operates strictly offline on your device. Your tasks stay in local Room SQLite storage with zero tracking or remote telemetry.", fontSize = 13.sp)
        }
      },
      confirmButton = {
        TextButton(onClick = { showPrivacyDialog = false }) {
          Text("Understood")
        }
      }
    )
  }

  if (showManageCategoriesDialog) {
    ManageCategoriesDialog(
      categories = categories,
      categoryTaskCounts = categoryTaskCounts,
      onAddCategory = onAddCategory,
      onRenameCategory = onRenameCategory,
      onDeleteCategory = onDeleteCategory,
      onDismiss = { showManageCategoriesDialog = false }
    )
  }
}

@Composable
private fun SettingsGroupCard(
  title: String,
  content: @Composable () -> Unit
) {
  val isDark = LocalIsDarkTheme.current

  Column {
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      fontSize = 14.sp,
      color = SlateGray,
      modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = if (isDark) SurfaceDark else SurfaceLight
      ),
      border = BorderStroke(1.dp, if (isDark) BorderDark else BorderLight),
      elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
    ) {
      Column(
        modifier = Modifier.padding(vertical = 4.dp)
      ) {
        content()
      }
    }
  }
}

@Composable
private fun SettingsRowItem(
  icon: ImageVector,
  iconTint: Color,
  title: String,
  subtitle: String? = null,
  value: String? = null,
  onClick: () -> Unit,
  enabled: Boolean = true,
  showArrow: Boolean = true,
  testTag: String = ""
) {
  val isDark = LocalIsDarkTheme.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .clickable(enabled = enabled) { onClick() }
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = iconTint,
      modifier = Modifier.size(22.dp)
    )

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        color = if (isDark) TextDarkPrimary else DarkNavyText
      )
      if (subtitle != null) {
        Text(
          text = subtitle,
          fontSize = 12.sp,
          color = SlateGray
        )
      }
    }

    if (value != null) {
      Text(
        text = value,
        fontSize = 13.5.sp,
        color = SlateGray,
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.width(6.dp))
    }

    if (showArrow) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = SlateGray,
        modifier = Modifier.size(18.dp)
      )
    }
  }
}

private fun createPdfFileName(): String {
  val timestamp = SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.US).format(Date())
  return "TaskEasy_Report_$timestamp.pdf"
}
