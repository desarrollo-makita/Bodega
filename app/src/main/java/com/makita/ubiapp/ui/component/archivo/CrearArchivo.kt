package com.makita.ubiapp.ui.component.archivo

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.makita.ubiapp.ui.component.entity.RegistraUbicacionEntity
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

suspend fun guardarDatosEnExcel(context: Context, registros: List<RegistraUbicacionEntity>): Uri? {
    return try {
        val fileName = "registros_backup.xlsx"
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Registros")

        // Crear la fila de encabezado
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Usuario")
        headerRow.createCell(1).setCellValue("Item")
        headerRow.createCell(2).setCellValue("Fecha de Cambio")
        headerRow.createCell(3).setCellValue("Tipo Item")
        headerRow.createCell(4).setCellValue("Ubicacion Antigua")
        headerRow.createCell(5).setCellValue("Nueva Ubicacion")

        // Rellenar los datos
        registros.forEachIndexed { index, registro ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(registro.username)
            row.createCell(1).setCellValue(registro.item)
            row.createCell(2).setCellValue(registro.timestamp)
            row.createCell(3).setCellValue(registro.tipoItem)
            row.createCell(4).setCellValue(registro.ubicacionAntigua)
            row.createCell(5).setCellValue(registro.nuevaUbicacion)
        }

        // Escribir el archivo
        val fileOut = FileOutputStream(filePath)
        workbook.write(fileOut)
        fileOut.close()
        workbook.close()

        Log.d("*MAKITA*", "Datos guardados en: ${filePath.absolutePath}")

        FileProvider.getUriForFile(context, "${context.packageName}.provider", filePath)
    } catch (e: Exception) {
        Log.e("*MAKITA*", "Error al guardar datos antes de borrar: ${e.message}")
        null
    }
}