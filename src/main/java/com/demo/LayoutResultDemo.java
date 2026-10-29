package com.demo;

import com.utils.HelperMethods;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.layout.*;
import com.itextpdf.layout.renderer.*;

public class LayoutResultDemo {
    static IRenderer root;
    static PdfDocument pdf;
    static Document doc;

    public static void main(String[] args) throws Exception {
        pdf = new PdfDocument(new PdfWriter("layout-results.pdf"));
        doc = new Document(pdf);
        root = new DemoRootRenderer(doc);

        addParagraph();
        addTable();
        addDiv();

        doc.close();
    }

    static void addParagraph() {
        Paragraph paragraph = new Paragraph(
                HelperMethods.readResourceAsOneLine("texts/messageForParagraph.txt"))
                .setBackgroundColor(ColorConstants.CYAN);

        tryToFit("Paragraph", paragraph, 400, 60, 5);
    }

    static void addTable() {
        Table table = new Table(2).useAllAvailableWidth()
                .setBackgroundColor(ColorConstants.YELLOW);
        for (int i = 1; i <= 8; i++) {
            table.addCell("Row " + i);
            table.addCell("Value " + i);
        }

        tryToFit("Table", table, 400, 60, 5);
    }

    static void addDiv() {
        Div div = new Div().setBackgroundColor(ColorConstants.ORANGE);
        div.add(new Paragraph("Block 1"));
        div.add(new Paragraph("Block 2"));
        div.add(new Paragraph("Block 3"));

        tryToFit("Div", div, 400, 45, 5);
        div.setKeepTogether(true);
        tryToFit("Div + keepTogether", div, 45);
    }

    static void tryToFit(String name, IBlockElement element, float... heights) {
        for (float height : heights) {
            fitOnce(name, element, height);
        }
    }

    static void fitOnce(String name, IBlockElement element, float height) {

        PdfPage page = pdf.addNewPage();
        int pageNum = pdf.getNumberOfPages();
        PdfCanvas canvas = new PdfCanvas(page);

        Rectangle thisArea__redRectangle = new Rectangle(36, 760 - height, 250, height);
        Rectangle nextArea__greyRectangle = new Rectangle(310, 360, 250, 400);

        IRenderer renderer = element.createRendererSubTree()
                .setParent(root);

        LayoutResult result = renderer.layout(
                new LayoutContext(new LayoutArea(pageNum, thisArea__redRectangle)));

        int layoutResStatus = result.getStatus();

        IRenderer fitsHere = layoutResStatus == LayoutResult.FULL ? renderer
                : layoutResStatus == LayoutResult.PARTIAL ? result.getSplitRenderer()
                        : null;
        IRenderer goesNext = layoutResStatus == LayoutResult.PARTIAL ? result.getOverflowRenderer()
                : layoutResStatus == LayoutResult.NOTHING ? renderer
                        : null;

        DrawContext draw = new DrawContext(pdf, canvas);

        if (fitsHere != null)
            fitsHere.draw(draw);
        if (goesNext != null) {
            goesNext.layout(new LayoutContext(new LayoutArea(pageNum, nextArea__greyRectangle)));
            goesNext.draw(draw);
        }

        canvas.setStrokeColor(ColorConstants.RED).rectangle(thisArea__redRectangle).stroke();
        canvas.setStrokeColor(ColorConstants.GRAY).rectangle(nextArea__greyRectangle).stroke();

        String label = name + ": " + height + "pt available -> " + statusName(layoutResStatus);

        try (Canvas labelCanvas = new Canvas(canvas, new Rectangle(36, 780, 520, 40))) {
            labelCanvas.add(new Paragraph(label));
        }

        System.out.println(label);
    }

    static String statusName(int layoutResStatus) {
        if (layoutResStatus == LayoutResult.FULL)
            return "FULL";
        if (layoutResStatus == LayoutResult.PARTIAL)
            return "PARTIAL";
        return "NOTHING";
    }


    static class DemoRootRenderer extends DocumentRenderer {
        DemoRootRenderer(Document document) {
            super(document);
            currentArea = new RootLayoutArea(1, new Rectangle(0, 0));
        }
    }
}