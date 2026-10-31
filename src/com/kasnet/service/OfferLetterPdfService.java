package com.kasnet.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.servlet.ServletContext;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class OfferLetterPdfService {

    private static final String TEMPLATE_PATH = "/WEB-INF/offer-letter-template.pdf";
    private static final int RENDER_DPI = 180;
    private static final DateTimeFormatter LONG_DATE = DateTimeFormatter.ofPattern("dd MMMM uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu", Locale.ENGLISH);

    public byte[] generate(ServletContext context, String name, String college, String branch,
                           String designation, int durationMonths, LocalDate startDate,
                           LocalDate offerDate) throws IOException {
        try (InputStream templateStream = context.getResourceAsStream(TEMPLATE_PATH);
             PDDocument template = loadTemplate(templateStream);
             PDDocument output = new PDDocument();
             ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            PDFRenderer renderer = new PDFRenderer(template);
            for (int pageIndex = 0; pageIndex < template.getNumberOfPages(); pageIndex++) {
                BufferedImage pageImage = renderer.renderImageWithDPI(pageIndex, RENDER_DPI, ImageType.RGB);
                replacePersonalFields(pageImage, pageIndex, name, college, branch, designation,
                        durationMonths, startDate, offerDate);
                PDRectangle pageSize = template.getPage(pageIndex).getMediaBox();
                PDPage page = new PDPage(pageSize);
                output.addPage(page);
                PDImageXObject flattenedPage = JPEGFactory.createFromImage(output, pageImage, 0.94f);
                try (PDPageContentStream content = new PDPageContentStream(output, page)) {
                    content.drawImage(flattenedPage, 0, 0, pageSize.getWidth(), pageSize.getHeight());
                }
                pageImage.flush();
            }
            output.save(bytes);
            return bytes.toByteArray();
        }
    }

    private PDDocument loadTemplate(InputStream input) throws IOException {
        if (input == null) throw new IOException("Offer letter template is missing from WEB-INF.");
        return PDDocument.load(input);
    }

    private void replacePersonalFields(BufferedImage image, int pageIndex, String name, String college,
                                       String branch, String designation, int months,
                                       LocalDate startDate, LocalDate offerDate) {
        Graphics2D graphics = image.createGraphics();
        float scale = RENDER_DPI / 72f;
        graphics.scale(scale, scale);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(new Font("Times New Roman", Font.PLAIN, 9));
        graphics.setColor(Color.WHITE);

        if (pageIndex == 0) {
            cover(graphics, 398, 174, 160, 14);
            cover(graphics, 68, 198, 470, 14);
            cover(graphics, 76, 277, 480, 14);
            cover(graphics, 76, 362, 490, 14);
            cover(graphics, 76, 374, 500, 14);

            graphics.setColor(Color.BLACK);
            drawFitted(graphics, "Date: " + SHORT_DATE.format(offerDate) + ".", 406, 182.3f, 145, 8.5f);
            drawFitted(graphics, "Mr. " + name, 72, 206.8f, 445, 8.5f);
            drawFitted(graphics, "\"" + designation + "\" in our organization w.e.f. " + LONG_DATE.format(startDate),
                80, 285.8f, 465, 8.5f);
            drawFitted(graphics, "Your internship will commence on " + LONG_DATE.format(startDate)
                + " and will continue for a period", 80, 370.5f, 475, 8.5f);
            LocalDate endDate = startDate.plusMonths(months).minusDays(1);
            drawFitted(graphics, "of " + durationWords(months) + ", ending on " + LONG_DATE.format(endDate)
                + ". During this period, you will work under", 80, 382.7f, 480, 8.5f);
        } else if (pageIndex == 2) {
            cover(graphics, 68, 268, 470, 14);
            cover(graphics, 68, 296, 500, 14);
            cover(graphics, 68, 334, 380, 14);
            cover(graphics, 68, 371, 300, 14);

            graphics.setColor(Color.BLACK);
            drawFitted(graphics, "Name - " + name, 72, 276.2f, 455, 8.5f);
            drawFitted(graphics, "College Name - " + college, 72, 304, 480, 8.5f);
            drawFitted(graphics, "Branch - " + branch, 72, 341.9f, 360, 8.5f);
            drawFitted(graphics, String.format(Locale.ROOT, "Duration - %02d months", months), 72, 379.1f, 280, 8.5f);
        }
        graphics.dispose();
    }

    private void cover(Graphics2D graphics, float x, float y, float width, float height) {
        graphics.fillRect(Math.round(x), Math.round(y), Math.round(width), Math.round(height));
    }

    private void drawFitted(Graphics2D graphics, String text, float x, float baseline, float maxWidth, float fontSize) {
        Font font = new Font("Times New Roman", Font.PLAIN, 9).deriveFont(fontSize);
        FontMetrics metrics = graphics.getFontMetrics(font);
        while (metrics.stringWidth(text) > maxWidth && fontSize > 6f) {
            fontSize -= 0.25f;
            font = font.deriveFont(fontSize);
            metrics = graphics.getFontMetrics(font);
        }
        graphics.setFont(font);
        graphics.drawString(text, x, baseline);
    }

    private String durationWords(int months) {
        String[] words = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};
        String count = months < words.length ? words[months] : Integer.toString(months);
        return count + (months == 1 ? " month" : " months");
    }
}