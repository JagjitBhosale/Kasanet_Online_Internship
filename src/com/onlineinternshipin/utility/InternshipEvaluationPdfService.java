package com.onlineinternshipin.utility;

import com.onlineinternshipin.model.InternshipEvaluationReport;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Generates the Internship Evaluation Report PDF, styled to match the
 * KasNet Technologies Pvt. Ltd. official letter-head format used by the
 * offer-letter template, complete with header, footer, company stamp and
 * director signature extracted from the offer-letter PDF.
 */
public class InternshipEvaluationPdfService {

    // ── layout constants (all in points; 1 pt = 1/72 inch) ─────────────────
    private static final int    RENDER_DPI    = 180;
    private static final float  PAGE_W        = PDRectangle.A4.getWidth();   // 595.28
    private static final float  PAGE_H        = PDRectangle.A4.getHeight();  // 841.89
    private static final float  MARGIN_LEFT   = 54f;
    private static final float  MARGIN_RIGHT  = 54f;
    private static final float  CONTENT_W     = PAGE_W - MARGIN_LEFT - MARGIN_RIGHT;
    private static final float  HEADER_H      = 70f;   // height reserved for letterhead
    private static final float  FOOTER_H      = 24f;   // height of orange footer bar
    private static final float  CONTENT_TOP   = HEADER_H + 14f;  // y where body text starts
    private static final float  CONTENT_BOT   = PAGE_H - FOOTER_H - 14f; // y where body ends

    // header image proportional height when scaled to CONTENT_W
    // actual image: 1299 × 130 → aspect ≈ 9.99 : 1
    private static final float  HEADER_IMG_H  = CONTENT_W / (1299f / 130f);

    // orange brand colour (#E8601C approximate)
    private static final Color  KASNET_ORANGE = new Color(0xE8, 0x60, 0x1C);
    private static final Color  KASNET_DARK   = new Color(0x27, 0x2F, 0x64);  // dark navy/indigo

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd MMMM uuuu", Locale.ENGLISH);

    // ── image paths relative to WEB-INF ─────────────────────────────────────
    private static final String PATH_HEADER    = "/WEB-INF/company-header.jpg";
    private static final String PATH_STAMP     = "/WEB-INF/company-stamp.jpg";
    private static final String PATH_SIGNATURE = "/WEB-INF/company-signature.jpg";

    // ── cached images (loaded once per servlet lifetime) ────────────────────
    private volatile BufferedImage cachedHeader;
    private volatile BufferedImage cachedStamp;
    private volatile BufferedImage cachedSignature;

    // ── public API ──────────────────────────────────────────────────────────

    /**
     * Generate the PDF bytes for the given report.
     *
     * @param context  servlet context used to load images from WEB-INF
     * @param report   populated evaluation report model
     */
    public byte[] generate(ServletContext context, InternshipEvaluationReport report) throws IOException {
        loadImages(context);
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            addPage(doc, renderPage1(report));
            addPage(doc, renderPage2(report));
            addPage(doc, renderPage3(report));

            doc.save(out);
            return out.toByteArray();
        }
    }

    // ── image loading ────────────────────────────────────────────────────────

    private void loadImages(ServletContext ctx) throws IOException {
        if (cachedHeader != null) return;
        synchronized (this) {
            if (cachedHeader != null) return;
            cachedHeader    = loadImage(ctx, PATH_HEADER);
            cachedStamp     = loadImage(ctx, PATH_STAMP);
            cachedSignature = loadImage(ctx, PATH_SIGNATURE);
        }
    }

    private static BufferedImage loadImage(ServletContext ctx, String path) throws IOException {
        try (InputStream in = ctx.getResourceAsStream(path)) {
            if (in == null) return null;
            return ImageIO.read(in);
        }
    }

    // ── PDF page helpers ─────────────────────────────────────────────────────

    private static void addPage(PDDocument doc, BufferedImage img) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        PDImageXObject xobj = JPEGFactory.createFromImage(doc, img, 0.93f);
        try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
            cs.drawImage(xobj, 0, 0, PAGE_W, PAGE_H);
        }
        img.flush();
    }

    // ── page renderers ───────────────────────────────────────────────────────

    /** Page 1: title, student info, cert details, evaluation table */
    private BufferedImage renderPage1(InternshipEvaluationReport report) {
        BufferedImage img = newPage();
        Graphics2D g = gfx(img);

        drawHeaderFooter(g);

        float y = CONTENT_TOP;

        // ── document title block ──────────────────────────────────────────
        y += 6;
        drawCentred(g, "INTERNSHIP EVALUATION REPORT & COMPANY RECOMMENDATION",
                y, font(Font.BOLD, 13), KASNET_DARK);
        y += 5;
        // orange underline
        g.setColor(KASNET_ORANGE);
        g.setStroke(new BasicStroke(1.4f));
        g.draw(new Line2D.Float(MARGIN_LEFT, y, PAGE_W - MARGIN_RIGHT, y));
        g.setColor(Color.BLACK);
        y += 14;

        // ── 1. Student Information ────────────────────────────────────────
        y = sectionHeader(g, "1.  Student Information", y);
        y = fieldRow(g, "Student Name",          report.studentName,       y);
        y = fieldRow(g, "College Name",           report.collegeName,       y);
        y = fieldRow(g, "Branch / Department",    report.branchDepartment,  y);
        y = fieldRow(g, "City",                   report.city,              y);
        y = fieldRow(g, "Internship Duration",    report.internshipDuration,y);
        y += 6;

        // ── 2. Global Certification Details ──────────────────────────────
        y = sectionHeader(g, "2.  Global Certification Details", y);
        y = fieldRow(g, "Global Certification Name", report.certificationName,  y);
        y = fieldRow(g, "Certification Score",        report.certificationScore, y);
        y = fieldRow(g, "Date of Achievement",
                DATE_FMT.format(report.certificationDate), y);
        y += 6;

        // ── 3. Internship Performance Evaluation ─────────────────────────
        y = sectionHeader(g, "3.  Internship Performance Evaluation", y);
        y += 2;
        y = drawEvaluationTable(g, report, y);
        y += 6;

        // overall rating line
        g.setFont(font(Font.BOLD, 10));
        g.setColor(KASNET_DARK);
        String ratingLabel = "Overall Rating:  " + report.overallRating + " / 5";
        g.drawString(ratingLabel, MARGIN_LEFT, y);
        // draw filled stars
        drawStars(g, report.overallRating, MARGIN_LEFT + g.getFontMetrics().stringWidth(ratingLabel) + 10, y - 9, 11);
        y += 4;

        g.dispose();
        return img;
    }

    /** Page 2: performance assessment paragraphs, recommendation paragraphs */
    private BufferedImage renderPage2(InternshipEvaluationReport report) {
        BufferedImage img = newPage();
        Graphics2D g = gfx(img);
        drawHeaderFooter(g);

        float y = CONTENT_TOP + 4;

        // ── 4. Overall Performance Assessment ────────────────────────────
        y = sectionHeader(g, "4.  Overall Performance Assessment", y);
        y += 2;
        y = paragraph(g, "The candidate has participated in the internship program conducted by KasNet "
                + "Technologies Pvt. Ltd. and has demonstrated commitment to learning, professional "
                + "discipline, and project-based development.", y);
        y += 6;
        y = paragraph(g, "The candidate\u2019s performance has been assessed based on attendance, punctuality, "
                + "understanding of project requirements, assignment completion, technical implementation, "
                + "documentation, presentation, and global certification achievements.", y);
        y += 6;
        y = paragraph(g, "The above ratings reflect the candidate\u2019s evaluated performance during the "
                + "internship period.", y);
        y += 14;

        // ── 5. Recommendation to the Company ─────────────────────────────
        y = sectionHeader(g, "5.  Recommendation to the Company", y);
        y += 2;
        y = paragraph(g, "Based on the candidate\u2019s internship evaluation, project work, technical "
                + "competencies, and professional conduct, KasNet Technologies Pvt. Ltd. recommends "
                + "the candidate for consideration for suitable entry-level employment opportunities.", y);
        y += 6;
        y = paragraph(g, "The candidate may be considered for relevant roles in Software Development, "
                + "Cloud Computing, Data Analytics, Business Intelligence, or other suitable IT domains, "
                + "depending on the candidate\u2019s demonstrated skills, qualifications, and the company\u2019s "
                + "requirements.", y);
        y += 6;
        y = paragraph(g, "We encourage the candidate to continue developing technical expertise, "
                + "communication skills, problem-solving abilities, and professional competencies to "
                + "succeed in the corporate environment.", y);

        g.dispose();
        return img;
    }

    /** Page 3: final recommendation fields, authorisation block with stamp */
    private BufferedImage renderPage3(InternshipEvaluationReport report) {
        BufferedImage img = newPage();
        Graphics2D g = gfx(img);
        drawHeaderFooter(g);

        float y = CONTENT_TOP + 4;

        // ── 6. Final Recommendation ───────────────────────────────────────
        y = sectionHeader(g, "6.  Final Recommendation", y);
        y = fieldRow(g, "Internship Status",        report.internshipStatus,        y);
        y = fieldRow(g, "Company Recommendation",   report.companyRecommendation,   y);
        y = fieldRow(g, "Key Strengths",            report.keyStrengths,            y);
        y = fieldRow(g, "Areas for Improvement",    report.improvementAreas,        y);
        y = fieldRow(g, "Evaluator\u2019s Remarks", report.evaluatorRemarks,        y);
        y = fieldRow(g, "Date of Evaluation",       DATE_FMT.format(report.evaluationDate), y);
        y += 18;

        // ── Authorisation block ───────────────────────────────────────────
        // "For KasNet Technologies Pvt. Ltd." label
        g.setFont(font(Font.BOLD, 10));
        g.setColor(KASNET_DARK);
        g.drawString("For KasNet Technologies Pvt. Ltd.", MARGIN_LEFT, y);
        y += 4;

        // signature image (left column) + stamp image (right column)
        float sigW  = 130f;
        float sigH  = cachedSignature != null ? sigW * cachedSignature.getHeight() / (float) cachedSignature.getWidth() : 60f;
        float stpSz = 100f;
        float sigX  = MARGIN_LEFT;
        float stpX  = PAGE_W - MARGIN_RIGHT - stpSz;
        float imgY  = y + 4;

        if (cachedSignature != null) {
            drawImage(g, cachedSignature, sigX, imgY, sigW, sigH);
        }
        if (cachedStamp != null) {
            drawImage(g, cachedStamp, stpX, imgY, stpSz, stpSz);
        }

        float afterImages = imgY + Math.max(sigH, stpSz) + 8;

        // evaluator details below signature
        g.setFont(font(Font.BOLD, 9));
        g.setColor(Color.BLACK);
        float ey = afterImages;
        ey = miniField(g, "Evaluator Name", report.evaluatorName, ey);
        ey = miniField(g, "Designation",    report.designation,   ey);
        ey = miniField(g, "Signature",      report.signature,     ey);

        g.dispose();
        return img;
    }

    // ── drawing primitives ───────────────────────────────────────────────────

    /** Draws the letterhead image header and orange footer bar. */
    private void drawHeaderFooter(Graphics2D g) {
        // ── header image ─────────────────────────────────────────────────
        if (cachedHeader != null) {
            drawImage(g, cachedHeader, MARGIN_LEFT, 10f, CONTENT_W, HEADER_IMG_H);
        } else {
            // fallback text header
            g.setFont(font(Font.BOLD, 14));
            g.setColor(KASNET_DARK);
            drawCentred(g, "KasNet Technologies Pvt. Ltd.", 36f, font(Font.BOLD, 14), KASNET_DARK);
        }
        // separator line under header (dark navy)
        g.setColor(KASNET_DARK);
        g.setStroke(new BasicStroke(1.1f));
        float sepY = HEADER_H + 1f;
        g.draw(new Line2D.Float(MARGIN_LEFT, sepY, PAGE_W - MARGIN_RIGHT, sepY));

        // ── orange footer bar ─────────────────────────────────────────────
        float barY = PAGE_H - FOOTER_H;
        g.setColor(KASNET_ORANGE);
        g.fillRect(Math.round(MARGIN_LEFT), Math.round(barY),
                Math.round(CONTENT_W), Math.round(FOOTER_H));
        // "www.kasnetinc.com" text
        g.setFont(font(Font.BOLD, 11));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String url = "www.kasnetinc.com";
        float tx = (PAGE_W - fm.stringWidth(url)) / 2f;
        g.drawString(url, tx, barY + FOOTER_H / 2f + fm.getAscent() / 2f - 1f);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(0.5f));
    }

    /** Section header: bold dark text with an orange left accent bar. */
    private float sectionHeader(Graphics2D g, String text, float y) {
        // orange left accent rect
        g.setColor(KASNET_ORANGE);
        g.fillRect(Math.round(MARGIN_LEFT), Math.round(y - 11f), 3, 15);
        // text
        g.setFont(font(Font.BOLD, 11));
        g.setColor(KASNET_DARK);
        g.drawString(text, MARGIN_LEFT + 8f, y);
        // thin divider line
        y += 3;
        g.setColor(new Color(0xCC, 0xCC, 0xCC));
        g.setStroke(new BasicStroke(0.4f));
        g.draw(new Line2D.Float(MARGIN_LEFT, y, PAGE_W - MARGIN_RIGHT, y));
        g.setColor(Color.BLACK);
        return y + 11;
    }

    /**
     * Draws a label : value row.  Returns the new y position.
     * Label is in dark bold; value follows on the same line after a fixed tab.
     */
    private float fieldRow(Graphics2D g, String label, String value, float y) {
        Font labelFont = font(Font.BOLD, 9);
        Font valueFont = font(Font.PLAIN, 9);

        float labelTabW = 148f;  // fixed label column width
        float valueX    = MARGIN_LEFT + labelTabW;
        float valueW    = PAGE_W - MARGIN_RIGHT - valueX;

        // label
        g.setFont(labelFont);
        g.setColor(KASNET_DARK);
        g.drawString(label + " :", MARGIN_LEFT, y);

        // value (may wrap)
        g.setFont(valueFont);
        g.setColor(Color.BLACK);
        List<String> lines = wrap(value == null ? "" : value, g.getFontMetrics(valueFont), valueW);
        if (lines.isEmpty()) lines.add("");

        float lineY = y;
        for (String line : lines) {
            g.drawString(line, valueX, lineY);
            // dotted underline
            g.setColor(new Color(0xBB, 0xBB, 0xBB));
            g.setStroke(new BasicStroke(0.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    1f, new float[]{2f, 2f}, 0f));
            g.draw(new Line2D.Float(valueX, lineY + 2.5f, valueX + valueW, lineY + 2.5f));
            g.setStroke(new BasicStroke(0.5f));
            g.setColor(Color.BLACK);
            lineY += 13f;
        }
        return Math.max(y + 17f, lineY + 2f);
    }

    /** Small label: value pair used in the evaluator block (compact). */
    private float miniField(Graphics2D g, String label, String value, float y) {
        g.setFont(font(Font.BOLD, 9));
        g.setColor(KASNET_DARK);
        g.drawString(label + ":", MARGIN_LEFT, y);
        g.setFont(font(Font.PLAIN, 9));
        g.setColor(Color.BLACK);
        g.drawString(value == null ? "" : value, MARGIN_LEFT + 90f, y);
        return y + 14f;
    }

    /** Draws a justified paragraph. Returns new y. */
    private float paragraph(Graphics2D g, String text, float y) {
        Font pFont = font(Font.PLAIN, 10);
        g.setFont(pFont);
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics(pFont);
        List<String> lines = wrap(text, fm, CONTENT_W);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            boolean isLast = (i == lines.size() - 1);
            if (isLast) {
                g.drawString(line, MARGIN_LEFT, y);
            } else {
                drawJustified(g, line, fm, MARGIN_LEFT, y, CONTENT_W);
            }
            y += 14f;
        }
        return y;
    }

    /** Draws a word-spaced justified line. */
    private void drawJustified(Graphics2D g, String line, FontMetrics fm, float x, float y, float maxW) {
        String[] words = line.trim().split(" +");
        if (words.length <= 1) {
            g.drawString(line, x, y);
            return;
        }
        int textW = fm.stringWidth(line.trim());
        float gap = (maxW - textW) / (words.length - 1);
        float cx = x;
        for (String word : words) {
            g.drawString(word, cx, y);
            cx += fm.stringWidth(word) + gap;
        }
    }

    /** Draws the evaluation criteria table. Returns y after the table. */
    private float drawEvaluationTable(Graphics2D g, InternshipEvaluationReport report, float top) {
        String[] CRITERIA = InternshipEvaluationReport.CRITERIA;
        int rows = CRITERIA.length + 1; // +1 header row
        float rowH = 19f;

        // column x boundaries
        float c0 = MARGIN_LEFT;
        float c1 = MARGIN_LEFT + CONTENT_W * 0.59f;   // ~59% for criteria
        float c2 = MARGIN_LEFT + CONTENT_W * 0.80f;   // ~21% for status
        float c3 = MARGIN_LEFT + CONTENT_W;            // ~20% for rating

        float tableH = rowH * rows;

        // header row background
        g.setColor(KASNET_DARK);
        g.fillRect(Math.round(c0), Math.round(top), Math.round(CONTENT_W), Math.round(rowH));

        // draw grid
        g.setColor(new Color(0xDD, 0xDD, 0xDD));
        g.setStroke(new BasicStroke(0.4f));
        // outer rect
        g.setColor(KASNET_DARK);
        g.setStroke(new BasicStroke(0.7f));
        g.drawRect(Math.round(c0), Math.round(top), Math.round(CONTENT_W), Math.round(tableH));
        // vertical dividers
        for (float cx : new float[]{c1, c2}) {
            g.draw(new Line2D.Float(cx, top, cx, top + tableH));
        }
        // horizontal row lines
        g.setColor(new Color(0xCC, 0xCC, 0xCC));
        g.setStroke(new BasicStroke(0.4f));
        for (int i = 1; i < rows; i++) {
            float ry = top + rowH * i;
            g.draw(new Line2D.Float(c0, ry, c0 + CONTENT_W, ry));
        }

        // header text (white)
        Font hFont = font(Font.BOLD, 8.5f);
        g.setColor(Color.WHITE);
        drawVCentredCell(g, "Evaluation Criteria", c0 + 5, top, c1 - c0 - 10, rowH, hFont);
        drawVCentredCell(g, "Status",              c1 + 4, top, c2 - c1 - 8,  rowH, hFont);
        drawVCentredCell(g, "Rating (1–5)",        c2 + 4, top, c3 - c2 - 8,  rowH, hFont);

        // alternating row colours
        Color rowAlt  = new Color(0xF4, 0xF7, 0xFF);
        Color rowBase = Color.WHITE;
        Font bFont = font(Font.PLAIN, 8.5f);

        for (int i = 0; i < CRITERIA.length; i++) {
            float ry = top + rowH * (i + 1);
            // fill alternating
            g.setColor(i % 2 == 0 ? rowBase : rowAlt);
            g.fillRect(Math.round(c0) + 1, Math.round(ry) + 1,
                    Math.round(CONTENT_W) - 1, Math.round(rowH) - 1);

            int rating = report.evaluations.get(i).rating;
            String status = report.evaluations.get(i).status;

            g.setColor(Color.BLACK);
            drawVCentredCell(g, CRITERIA[i],          c0 + 5, ry, c1 - c0 - 10, rowH, bFont);
            drawVCentredCell(g, status,                c1 + 4, ry, c2 - c1 - 8,  rowH, bFont);

            // coloured rating badge
            drawRatingBadge(g, rating, c2, ry, c3 - c2, rowH);
        }

        return top + tableH;
    }

    /** Draws a small coloured rating badge centred in its cell. */
    private void drawRatingBadge(Graphics2D g, int rating, float cellX, float cellY,
                                  float cellW, float cellH) {
        Color bg;
        if (rating >= 5)      bg = new Color(0x27, 0xAE, 0x60); // green
        else if (rating >= 4) bg = new Color(0x2E, 0x86, 0xC1); // blue
        else if (rating >= 3) bg = new Color(0xF3, 0x9C, 0x12); // amber
        else                  bg = new Color(0xC0, 0x39, 0x2B); // red

        float bW = 20f, bH = 11f;
        float bX = cellX + (cellW - bW) / 2f;
        float bY = cellY + (cellH - bH) / 2f;

        g.setColor(bg);
        g.fillRoundRect(Math.round(bX), Math.round(bY), Math.round(bW), Math.round(bH), 4, 4);
        g.setFont(font(Font.BOLD, 8f));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String txt = String.valueOf(rating);
        g.drawString(txt,
                bX + (bW - fm.stringWidth(txt)) / 2f,
                bY + (bH + fm.getAscent() - fm.getDescent()) / 2f - 1f);
    }

    /** Draws text vertically centred within a table cell. */
    private void drawVCentredCell(Graphics2D g, String text, float x, float rowTop,
                                   float maxW, float rowH, Font f) {
        Font fitted = f;
        float sz = f.getSize2D();
        FontMetrics fm = g.getFontMetrics(fitted);
        while (fm.stringWidth(text) > maxW && sz > 6f) {
            sz = Math.max(6f, sz - 0.5f);
            fitted = f.deriveFont(sz);
            fm = g.getFontMetrics(fitted);
        }
        g.setFont(fitted);
        g.drawString(text, x, rowTop + (rowH + fm.getAscent() - fm.getDescent()) / 2f - 1f);
    }

    /** Draws five-pointed filled stars for the rating. */
    private void drawStars(Graphics2D g, int rating, float x, float y, float size) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < 5; i++) {
            g.setColor(i < rating ? new Color(0xF3, 0x9C, 0x12) : new Color(0xCC, 0xCC, 0xCC));
            drawStar(g, x + i * (size + 2), y, size);
        }
    }

    private void drawStar(Graphics2D g, float cx, float cy, float size) {
        int n = 5;
        float outer = size / 2f;
        float inner = outer * 0.4f;
        int[] px = new int[n * 2], py = new int[n * 2];
        for (int i = 0; i < n * 2; i++) {
            double angle = Math.PI / 2 + i * Math.PI / n;
            float r = (i % 2 == 0) ? outer : inner;
            px[i] = Math.round(cx + r * (float) Math.cos(angle));
            py[i] = Math.round(cy + size / 2f - r * (float) Math.sin(angle));
        }
        g.fillPolygon(px, py, n * 2);
    }

    /** Draws a BufferedImage at (x,y) with given width/height in points. */
    private void drawImage(Graphics2D g, BufferedImage img,
                            float x, float y, float w, float h) {
        g.drawImage(img, Math.round(x), Math.round(y), Math.round(w), Math.round(h), null);
    }

    /** Centres text horizontally on the page. */
    private void drawCentred(Graphics2D g, String text, float y, Font f, Color c) {
        g.setFont(f);
        g.setColor(c);
        FontMetrics fm = g.getFontMetrics(f);
        g.drawString(text, (PAGE_W - fm.stringWidth(text)) / 2f, y);
    }

    // ── utility ──────────────────────────────────────────────────────────────

    /** Creates a blank white A4-sized BufferedImage at RENDER_DPI. */
    private static BufferedImage newPage() {
        float scale = RENDER_DPI / 72f;
        BufferedImage img = new BufferedImage(
                (int) Math.ceil(PAGE_W * scale),
                (int) Math.ceil(PAGE_H * scale),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.dispose();
        return img;
    }

    /** Creates a Graphics2D that draws in point-space at RENDER_DPI. */
    private static Graphics2D gfx(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        float s = RENDER_DPI / 72f;
        g.scale(s, s);
        g.setColor(Color.BLACK);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        g.setStroke(new BasicStroke(0.5f));
        return g;
    }

    private static Font font(int style, float size) {
        return new Font("Times New Roman", style, 10).deriveFont(style, size);
    }

    private static List<String> wrap(String text, FontMetrics fm, float maxW) {
        List<String> out = new ArrayList<>();
        if (text == null || text.isEmpty()) return out;
        String normalized = text.replace('\r', ' ').replace('\n', ' ').trim();
        StringBuilder line = new StringBuilder();
        for (String word : normalized.split("\\s+")) {
            if (line.length() == 0) {
                line.append(word);
            } else {
                String candidate = line + " " + word;
                if (fm.stringWidth(candidate) <= maxW) {
                    line.append(' ').append(word);
                } else {
                    out.add(line.toString());
                    line.setLength(0);
                    // handle word wider than maxW
                    while (fm.stringWidth(word) > maxW) {
                        int split = 1;
                        while (split < word.length() && fm.stringWidth(word.substring(0, split + 1)) <= maxW)
                            split++;
                        out.add(word.substring(0, split));
                        word = word.substring(split);
                    }
                    line.append(word);
                }
            }
        }
        if (line.length() > 0) out.add(line.toString());
        return out;
    }
}
