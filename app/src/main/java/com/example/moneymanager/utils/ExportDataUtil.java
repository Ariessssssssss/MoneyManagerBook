package com.example.moneymanager.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.moneymanager.model.dao.DBManager;
import com.example.moneymanager.model.service.DetailsService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ExportDataUtil {
    /**
     * Export detailstb data to CSV file
     *
     * @param context Application context
     * @param uri Destination file URI
     * @return true if export was successful
     */
    private static DetailsService detailsService;
    public static boolean exportToCSV(Context context, Uri uri) {
        try {
            Cursor cursor = detailsService.getAllDetailsCursor();
            if (cursor == null || cursor.getCount() == 0) {
                Toast.makeText(context, "没有数据可以导出", Toast.LENGTH_SHORT).show();
                return false;
            }

            FileOutputStream outputStream = (FileOutputStream) context.getContentResolver().openOutputStream(uri);

            // Get column names and create user-friendly headers
            Map<String, String> columnLabels = getColumnLabels();
            // Write CSV header
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                if (i > 0) header.append(",");
                String columnName = cursor.getColumnName(i);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    header.append(columnLabels.getOrDefault(columnName, columnName));
                }
            }
            header.append("\n");
            outputStream.write(header.toString().getBytes());

            // Write data rows
            while (cursor.moveToNext()) {
                StringBuilder row = new StringBuilder();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (i > 0) row.append(",");
                    String value = cursor.getString(i);
                    // Format kind values (0=支出, 1=收入)
                    if (cursor.getColumnName(i).equals("kind")) {
                        value = (value.equals("0")) ? "支出" : "收入";
                    }
                    // Escape commas in the data
                    if (value != null && value.contains(",")) {
                        value = "\"" + value + "\"";
                    }
                    row.append(value == null ? "" : value);
                }
                row.append("\n");
                outputStream.write(row.toString().getBytes());
            }

            outputStream.close();
            cursor.close();

            Toast.makeText(context, "成功导出为CSV文件", Toast.LENGTH_SHORT).show();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "导出CSV失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Export data to PDF file
     *
     * @param context Application context
     * @param uri Destination file URI
     * @return true if export was successful
     */
    public static boolean exportToPDF(Context context, Uri uri) {
        try {
            Cursor cursor = detailsService.getAllDetailsCursor();
            if (cursor == null || cursor.getCount() == 0) {
                Toast.makeText(context, "没有数据可以导出", Toast.LENGTH_SHORT).show();
                return false;
            }

            Document document = new Document();
            PdfWriter.getInstance(document, context.getContentResolver().openOutputStream(uri));

            document.open();

            // Create fonts with fallback to system fonts
            Font titleFont = createChineseFontFallback(context,18, Font.BOLD);
            Font headerFont = createChineseFontFallback(context,12, Font.BOLD);
            Font contentFont = createChineseFontFallback(context,10, Font.NORMAL);

            Paragraph title = new Paragraph("Money Manager 数据导出", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Add some space

            // Get user-friendly column names
            Map<String, String> columnLabels = getColumnLabels();

            // Create table with only relevant columns
            String[] displayColumns = {"id", "typename", "money", "time", "record", "kind"};
            PdfPTable table = new PdfPTable(displayColumns.length);
            table.setWidthPercentage(100);

            // Add header row with friendly names
            for (String columnName : displayColumns) {
                String displayName = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    displayName = columnLabels.getOrDefault(columnName, columnName);
                }
                PdfPCell cell = new PdfPCell(new Phrase(displayName, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            // Get indices for each column
            Map<String, Integer> columnIndices = new HashMap<>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                columnIndices.put(cursor.getColumnName(i), i);
            }

            // Add data rows
            while (cursor.moveToNext()) {
                for (String columnName : displayColumns) {
                    Integer index = columnIndices.get(columnName);
                    if (index != null) {
                        String value = cursor.getString(index);

                        // Format kind values (0=支出, 1=收入)
                        if (columnName.equals("kind")) {
                            value = (value.equals("0")) ? "支出" : "收入";
                        }

                        PdfPCell cell = new PdfPCell(new Phrase(value == null ? "" : value, contentFont));
                        if (columnName.equals("money")) {
                            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        } else if (columnName.equals("id")) {
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        } else {
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        }
                        table.addCell(cell);
                    }
                }
            }
            document.add(table);

            // Add summary information
            cursor.moveToPosition(-1); // Reset cursor position
            double totalIncome = 0;
            double totalExpense = 0;
            while (cursor.moveToNext()) {
                int kindIndex = cursor.getColumnIndex("kind");
                int moneyIndex = cursor.getColumnIndex("money");

                if (kindIndex >= 0 && moneyIndex >= 0) {
                    int kind = cursor.getInt(kindIndex);
                    double money = cursor.getDouble(moneyIndex);

                    if (kind == 0) {
                        totalExpense += money;
                    } else {
                        totalIncome += money;
                    }
                }
            }

            document.add(new Paragraph(" ")); // Add some space
            document.add(new Paragraph("总收入: " + String.format("%.2f", totalIncome), headerFont));
            document.add(new Paragraph("总支出: " + String.format("%.2f", totalExpense), headerFont));
            document.add(new Paragraph("结余: " + String.format("%.2f", totalIncome - totalExpense), headerFont));

            document.close();
            cursor.close();

            Toast.makeText(context, "成功导出为PDF文件", Toast.LENGTH_SHORT).show();
            return true;
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            Toast.makeText(context, "导出PDF失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    /**
     * Creates a font that supports Chinese characters
     */
    private static Font createChineseFontFallback(Context context,int size, int style) throws DocumentException, IOException {
        try {
            InputStream fontStream = context.getAssets().open("noto_sans_sc.ttf");

            // Create a temporary file
            File tempFont = File.createTempFile("temp_font", ".ttf", context.getCacheDir());
            tempFont.deleteOnExit();

            // Copy the font to the temporary file
            try (FileOutputStream out = new FileOutputStream(tempFont)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = fontStream.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
            BaseFont baseFont = BaseFont.createFont(tempFont.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(baseFont, size, style);
        } catch (DocumentException e) {
            // Fallback to default font if Chinese font fails
            Log.w("ExportDataUtil", "Failed to load Chinese font, using default font");
            return new Font(Font.FontFamily.HELVETICA, size, style);
        }
    }

    /**
     * Returns user-friendly column labels for the detailstb table
     */
    private static Map<String, String> getColumnLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("id", "ID");
        labels.put("typename", "类别");
        labels.put("sImageId", "图标ID");
        labels.put("record", "备注");
        labels.put("money", "金额");
        labels.put("time", "时间");
        labels.put("year", "年");
        labels.put("month", "月");
        labels.put("day", "日");
        labels.put("kind", "类型"); // 0=支出, 1=收入
        return labels;
    }
}
