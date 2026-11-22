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
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private static final float  CONTENT_W     = PAGE_W - MARGIN_LEFT - MARGIN_RIGHT; // 487.28
    private static final float  HEADER_H      = 70f;   // height reserved for letterhead
    private static final float  FOOTER_H      = 24f;   // height of orange footer bar
    private static final float  CONTENT_TOP   = HEADER_H + 16f;  // y where body text starts

    // header image proportional height when scaled to CONTENT_W
    // actual image: 1299 × 130 → aspect ≈ 9.99 : 1
    private static final float  HEADER_IMG_H  = CONTENT_W / (1299f / 130f);

    // brand colours
    private static final Color  KASNET_ORANGE = new Color(0xE8, 0x60, 0x1C);
    private static final Color  KASNET_DARK   = new Color(0x27, 0x2F, 0x64);  // dark navy/indigo
    private static final Color  BORDER_COLOR  = new Color(0xBB, 0xC3, 0xD4);  // clean professional table border
    private static final Color  LINE_MUTED    = new Color(0xCC, 0xD2, 0xDE);  // subtle divider line

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
     * @param context  servlet context used to load images from WEB-INF (or null for local testing)
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
        if (ctx != null) {
            try (InputStream in = ctx.getResourceAsStream(path)) {
                if (in != null) return ImageIO.read(in);
            }
        }
        File fallback = new File("WebContent" + path);
        if (fallback.exists()) {
            return ImageIO.read(fallback);
        }
        return null;
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

        float y = CONTENT_TOP + 6f;

        // ── document title block ──────────────────────────────────────────
        Font titleFont = font(Font.BOLD, 12.5f);
        drawCentred(g, "INTERNSHIP EVALUATION REPORT & COMPANY RECOMMENDATION",
                y, titleFont, KASNET_DARK);
        y += 6f;
        // orange underline
        g.setColor(KASNET_ORANGE);
        g.setStroke(new BasicStroke(1.4f));
        g.draw(new Line2D.Float(MARGIN_LEFT, y, PAGE_W - MARGIN_RIGHT, y));
        g.setColor(Color.BLACK);
        y += 16f;

        // ── 1. Student Information ────────────────────────────────────────
        float colonX = MARGIN_LEFT + 155f;
        y = sectionHeader(g, "1.  Student Information", y);
        y = alignedFieldRow(g, "Student Name",          report.studentName,        y, colonX);
        y = alignedFieldRow(g, "College Name",           report.collegeName,        y, colonX);
        y = alignedFieldRow(g, "Branch / Department",    report.branchDepartment,   y, colonX);
        y = alignedFieldRow(g, "City",                   report.city,               y, colonX);
        y = alignedFieldRow(g, "Internship Duration",    report.internshipDuration, y, colonX);
        y += 6f;

        // ── 2. Global Certification Details ──────────────────────────────
        y = sectionHeader(g, "2.  Global Certification Details", y);
        y = alignedFieldRow(g, "Global Certification Name", report.certificationName,  y, colonX);
        y = alignedFieldRow(g, "Certification Score",        report.certificationScore, y, colonX);
        y = alignedFieldRow(g, "Date of Achievement",
                DATE_FMT.format(report.certificationDate), y, colonX);
        y += 6f;

        // ── 3. Internship Performance Evaluation ─────────────────────────
        y = sectionHeader(g, "3.  Internship Performance Evaluation", y);
        y += 2f;
        y = drawEvaluationTable(g, report, y);
        y += 20f;   // generous gap between table bottom and overall rating

        // ── Overall Rating row ────────────────────────────────────────────
        Font ratingFont = font(Font.BOLD, 10.5f);
        g.setFont(ratingFont);
        g.setColor(KASNET_DARK);
        FontMetrics rFm = g.getFontMetrics(ratingFont);
        String ratingLabel = "Overall Rating:   " + report.overallRating + " / 5";
        g.drawString(ratingLabel, MARGIN_LEFT, y);

        // draw 5 geometric vector stars aligned with cap-height center
        float labelWidth = rFm.stringWidth(ratingLabel);
        float starsX = MARGIN_LEFT + labelWidth + 14f;
        float starRadius = 6.2f; // ~12.4 pt diameter
        float starCenterY = y - rFm.getAscent() / 2f + 0.5f;
        drawStars(g, report.overallRating, starsX, starCenterY, starRadius, 4.5f);

        g.dispose();
        return img;
    }

    /** Page 2: performance assessment paragraphs, recommendation paragraphs */
    private BufferedImage renderPage2(InternshipEvaluationReport report) {
        BufferedImage img = newPage();
        Graphics2D g = gfx(img);
        drawHeaderFooter(g);

        float y = CONTENT_TOP + 10f;

        // ── 4. Overall Performance Assessment ────────────────────────────
        y = sectionHeader(g, "4.  Overall Performance Assessment", y);
        y += 6f;
        y = paragraph(g, "The candidate has participated in the internship program conducted by KasNet "
                + "Technologies Pvt. Ltd. and has demonstrated commitment to learning, professional "
                + "discipline, and project-based development.", y);
        y += 10f;
        y = paragraph(g, "The candidate's performance has been assessed based on attendance, punctuality, "
                + "understanding of project requirements, assignment completion, technical implementation, "
                + "documentation, presentation, and global certification achievements.", y);
        y += 10f;
        y = paragraph(g, "The above ratings reflect the candidate's evaluated performance during the "
                + "internship period.", y);
        y += 24f;

        // ── 5. Recommendation to the Company ─────────────────────────────
        y = sectionHeader(g, "5.  Recommendation to the Company", y);
        y += 6f;
        y = paragraph(g, "Based on the candidate's internship evaluation, project work, technical "
                + "competencies, and professional conduct, KasNet Technologies Pvt. Ltd. recommends "
                + "the candidate for consideration for suitable entry-level employment opportunities.", y);
        y += 10f;
        y = paragraph(g, "The candidate may be considered for relevant roles in Software Development, "
                + "Cloud Computing, Data Analytics, Business Intelligence, or other suitable IT domains, "
                + "depending on the candidate's demonstrated skills, qualifications, and the company's "
                + "requirements.", y);
        y += 10f;
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

        float y = CONTENT_TOP + 10f;

        // ── 6. Final Recommendation ───────────────────────────────────────
        float colonX6 = MARGIN_LEFT + 155f;
        y = sectionHeader(g, "6.  Final Recommendation", y);
        y = alignedFieldRow(g, "Internship Status",        report.internshipStatus,        y, colonX6);
        y = alignedFieldRow(g, "Company Recommendation",   report.companyRecommendation,   y, colonX6);
        y = alignedFieldRow(g, "Key Strengths",            report.keyStrengths,            y, colonX6);
        y = alignedFieldRow(g, "Areas for Improvement",    report.improvementAreas,        y, colonX6);
        y = alignedFieldRow(g, "Evaluator's Remarks",      report.evaluatorRemarks,        y, colonX6);
        y = alignedFieldRow(g, "Date of Evaluation",       DATE_FMT.format(report.evaluationDate), y, colonX6);
        y += 28f;

        // ── Authorisation block ───────────────────────────────────────────
        float sigW = 145f;
        float sigH = cachedSignature != null
                ? sigW * cachedSignature.getHeight() / (float) cachedSignature.getWidth()
                : 65f;
        float stpSz = 102f;
        float sigX  = MARGIN_LEFT;
        float stpX  = PAGE_W - MARGIN_RIGHT - stpSz;
        float imgY  = y;

        // If signature image is missing, draw text fallback; if present, the image already has "For KasNet..."
        if (cachedSignature != null) {
            drawImage(g, cachedSignature, sigX, imgY, sigW, sigH);
        } else {
            g.setFont(font(Font.BOLD, 10));
            g.setColor(KASNET_DARK);
            g.drawString("For KasNet Technologies Pvt. Ltd.", sigX, imgY + 12f);
        }

        // Company Stamp on the right
        if (cachedStamp != null) {
            drawImage(g, cachedStamp, stpX, imgY - 8f, stpSz, stpSz);
        }

        float afterImages = imgY + Math.max(sigH, stpSz) + 12f;

        // evaluator details below signature
        float ey = afterImages;
        float evalColonX = MARGIN_LEFT + 105f;
        ey = alignedFieldRow(g, "Evaluator Name", report.evaluatorName, ey, evalColonX);
        ey = alignedFieldRow(g, "Designation",    report.designation,   ey, evalColonX);
        ey = alignedFieldRow(g, "Signature",      report.signature,     ey, evalColonX);

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
        g.drawString(url, tx, barY + FOOTER_H / 2f + fm.getAscent() / 2f - 1.5f);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(0.5f));
    }

    /** Section header: bold dark text with an orange left accent bar. */
    private float sectionHeader(Graphics2D g, String text, float y) {
        Font hFont = font(Font.BOLD, 11);
        FontMetrics hFm = g.getFontMetrics(hFont);
        int ascent  = hFm.getAscent();
        int descent = hFm.getDescent();
        int barH    = ascent + descent + 2;

        // orange left accent bar
        g.setColor(KASNET_ORANGE);
        g.fillRect(Math.round(MARGIN_LEFT), Math.round(y - ascent - 1), 3, barH);

        // header title
        g.setFont(hFont);
        g.setColor(KASNET_DARK);
        g.drawString(text, MARGIN_LEFT + 8f, y);

        // subtle horizontal line underneath
        float divY = y + descent + 4f;
        g.setColor(LINE_MUTED);
        g.setStroke(new BasicStroke(0.5f));
        g.draw(new Line2D.Float(MARGIN_LEFT, divY, PAGE_W - MARGIN_RIGHT, divY));
        g.setColor(Color.BLACK);
        return divY + 10f;
    }

    /**
     * Draws a label : value row where all colons align at the exact same horizontal position colonX.
     * Label is bold dark; colon is bold dark; value is plain black with dotted underline.
     */
    private float alignedFieldRow(Graphics2D g, String label, String value, float y, float colonX) {
        Font labelFont = font(Font.BOLD, 9);
        Font valueFont = font(Font.PLAIN, 9);

        float valueX = colonX + 12f;
        float valueW = PAGE_W - MARGIN_RIGHT - valueX;

        FontMetrics vFm = g.getFontMetrics(valueFont);
        float baseline = y;

        // draw label
        g.setFont(labelFont);
        g.setColor(KASNET_DARK);
        g.drawString(label, MARGIN_LEFT, baseline);

        // draw colon at the exact fixed position
        g.drawString(":", colonX, baseline);

        // draw value (may wrap across multiple lines)
        g.setFont(valueFont);
        g.setColor(Color.BLACK);
        List<String> lines = wrap(value == null ? "" : value, vFm, valueW);
        if (lines.isEmpty()) lines.add("");

        float lineY = baseline;
        float lineSpacing = 14f;
        for (String line : lines) {
            g.drawString(line, valueX, lineY);
            // subtle dotted underline
            g.setColor(new Color(0xC5, 0xCB, 0xD6));
            g.setStroke(new BasicStroke(0.4f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    1f, new float[]{2f, 2f}, 0f));
            g.draw(new Line2D.Float(valueX, lineY + 3f, valueX + valueW, lineY + 3f));
            g.setStroke(new BasicStroke(0.5f));
            g.setColor(Color.BLACK);
            lineY += lineSpacing;
        }

        return Math.max(baseline + 17f, lineY + 1f);
    }

    /** Draws a clean paragraph with proper word spacing and line height. */
    private float paragraph(Graphics2D g, String text, float y) {
        Font pFont = font(Font.PLAIN, 9.8f);
        g.setFont(pFont);
        g.setColor(new Color(0x1F, 0x24, 0x30));
        FontMetrics fm = g.getFontMetrics(pFont);
        float lineSpacing = 15.5f;
        List<String> lines = wrap(text, fm, CONTENT_W);
        for (String line : lines) {
            g.drawString(line, MARGIN_LEFT, y);
            y += lineSpacing;
        }
        return y;
    }

    /**
     * Draws the evaluation criteria table with continuous, unbroken grid lines
     * and perfectly centered badges and labels.
     */
    private float drawEvaluationTable(Graphics2D g, InternshipEvaluationReport report, float top) {
        String[] CRITERIA = InternshipEvaluationReport.CRITERIA;
        int rows = CRITERIA.length + 1; // +1 header row
        float rowH = 22.5f;             // spacious, professional row height

        // column x boundaries
        float c0 = MARGIN_LEFT;
        float c1 = MARGIN_LEFT + CONTENT_W * 0.58f;   // ~58% for criteria
        float c2 = MARGIN_LEFT + CONTENT_W * 0.81f;   // ~23% for status
        float c3 = MARGIN_LEFT + CONTENT_W;            // ~19% for rating

        float tableH = rowH * rows;

        // 1. FILL ALL ROW BACKGROUNDS FIRST
        // Header background (Navy)
        g.setColor(KASNET_DARK);
        g.fillRect(Math.round(c0), Math.round(top), Math.round(CONTENT_W), Math.round(rowH));

        // Data rows (alternating soft tint)
        Color rowAlt  = new Color(0xF4, 0xF7, 0xFA);
        Color rowBase = Color.WHITE;
        for (int i = 0; i < CRITERIA.length; i++) {
            float ry = top + rowH * (i + 1);
            g.setColor(i % 2 == 0 ? rowBase : rowAlt);
            g.fillRect(Math.round(c0), Math.round(ry), Math.round(CONTENT_W), Math.round(rowH));
        }

        // 2. DRAW ALL GRID LINES ON TOP (so vertical lines are unbroken)
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(0.6f));

        // Outer border
        g.drawRect(Math.round(c0), Math.round(top), Math.round(CONTENT_W), Math.round(tableH));

        // Vertical dividers (continuous from top to bottom)
        g.draw(new Line2D.Float(c1, top, c1, top + tableH));
        g.draw(new Line2D.Float(c2, top, c2, top + tableH));

        // Horizontal dividers
        for (int i = 1; i < rows; i++) {
            float ry = top + rowH * i;
            g.draw(new Line2D.Float(c0, ry, c0 + CONTENT_W, ry));
        }

        // 3. DRAW HEADER TEXT
        Font hFont = font(Font.BOLD, 8.5f);
        g.setFont(hFont);
        g.setColor(Color.WHITE);
        FontMetrics hFm = g.getFontMetrics(hFont);
        float hBaseY = top + (rowH - (hFm.getAscent() + hFm.getDescent())) / 2f + hFm.getAscent();

        // Col 1: Evaluation Criteria (left-aligned with 10pt indent)
        g.drawString("Evaluation Criteria", c0 + 10f, hBaseY);

        // Col 2: Status (centered in column 2)
        String hStatus = "Status";
        float hStatusX = c1 + (c2 - c1 - hFm.stringWidth(hStatus)) / 2f;
        g.drawString(hStatus, hStatusX, hBaseY);

        // Col 3: Rating (1-5) (centered in column 3)
        String hRating = "Rating (1–5)";
        float hRatingX = c2 + (c3 - c2 - hFm.stringWidth(hRating)) / 2f;
        g.drawString(hRating, hRatingX, hBaseY);

        // 4. DRAW ROW CONTENT
        Font bFont = font(Font.PLAIN, 8.8f);
        g.setFont(bFont);
        FontMetrics bFm = g.getFontMetrics(bFont);
        float lineH = bFm.getAscent() + bFm.getDescent();

        for (int i = 0; i < CRITERIA.length; i++) {
            float ry = top + rowH * (i + 1);
            float rowBaseY = ry + (rowH - lineH) / 2f + bFm.getAscent();

            String crit = CRITERIA[i];
            String stat = report.evaluations.get(i).status;
            int    rate = report.evaluations.get(i).rating;

            // Col 1: Criteria text
            g.setFont(bFont);
            g.setColor(new Color(0x1F, 0x24, 0x30));
            g.drawString(crit, c0 + 10f, rowBaseY);

            // Col 2: Status (centered in column 2)
            float statW = bFm.stringWidth(stat);
            float statX = c1 + (c2 - c1 - statW) / 2f;
            g.drawString(stat, statX, rowBaseY);

            // Col 3: Rating badge (centered pill)
            drawRatingBadge(g, rate, c2, ry, c3 - c2, rowH);
        }

        return top + tableH;
    }

    /**
     * Draws a perfectly centered rating badge pill inside a cell.
     */
    private void drawRatingBadge(Graphics2D g, int rating, float cellX, float cellY,
                                  float cellW, float cellH) {
        Color bg;
        if (rating >= 5)      bg = new Color(0x27, 0xAE, 0x60); // green
        else if (rating >= 4) bg = new Color(0x29, 0x80, 0xB9); // blue
        else if (rating >= 3) bg = new Color(0xF3, 0x9C, 0x12); // amber
        else                  bg = new Color(0xC0, 0x39, 0x2B); // red

        float bW = 28f, bH = 15f;
        float bX = cellX + (cellW - bW) / 2f;
        float bY = cellY + (cellH - bH) / 2f;

        g.setColor(bg);
        g.fillRoundRect(Math.round(bX), Math.round(bY), Math.round(bW), Math.round(bH), 6, 6);

        // draw number dead-centered
        Font badgeFont = font(Font.BOLD, 8.5f);
        g.setFont(badgeFont);
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics(badgeFont);
        String txt = String.valueOf(rating);
        float tx = bX + (bW - fm.stringWidth(txt)) / 2f;
        float ty = bY + (bH - (fm.getAscent() + fm.getDescent())) / 2f + fm.getAscent();
        g.drawString(txt, tx, ty);
    }

    /**
     * Draws rating stars as smooth geometric vector paths centered vertically at centerY.
     */
    private void drawStars(Graphics2D g, int rating, float startX, float centerY, float radius, float gap) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color gold = new Color(0xF3, 0x9C, 0x12);
        Color gray = new Color(0xDB, 0xE0, 0xEA);

        for (int i = 0; i < 5; i++) {
            float cx = startX + i * (radius * 2f + gap) + radius;
            g.setColor(i < rating ? gold : gray);
            drawStar(g, cx, centerY, radius);
        }
    }

    /**
     * Draws a single five-pointed star centered at (cx, cy) using exact golden ratio proportions.
     */
    private void drawStar(Graphics2D g, float cx, float cy, float radius) {
        Path2D.Float path = new Path2D.Float();
        int n = 5;
        float innerRadius = radius * 0.382f; // golden ratio star inner radius
        for (int i = 0; i < n * 2; i++) {
            double angle = Math.PI / 2.0 - i * Math.PI / n;
            float r = (i % 2 == 0) ? radius : innerRadius;
            float x = cx + r * (float) Math.cos(angle);
            float y = cy - r * (float) Math.sin(angle);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.closePath();
        g.fill(path);
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
