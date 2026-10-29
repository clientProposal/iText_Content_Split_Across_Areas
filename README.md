# iText Content Split Across Areas

A small Java demo that shows how iText 7 decides whether a layout element fits into a given area, and what happens when it doesn't.

Each element is laid out into a fixed **red rectangle**. Depending on how much space it gets, `layout()` returns one of three `LayoutResult` statuses:

| Status | Meaning | What the demo does |
|---|---|---|
| `FULL` | The whole element fits | Draws it in the red area |
| `PARTIAL` | Only part of it fits | Draws the part that fits in the red area. The rest is laid out again and drawn in the **grey rectangle** (the "next area") |
| `NOTHING` | None of it fits | Moves the whole element to the grey area (`goesNext = renderer`) |

The results are written to `layout-results-2.pdf`. Each page shows one attempt and has a label with the element type and status.

## What it demonstrates

- Laying out and drawing elements **manually** with `layout()` and `draw()`, without calling `Document.add()`
- How `Paragraph`, `Table` and `Div` behave when space runs out
- How a renderer splits into a part that fits here (`fitsHere`) and a part that goes next (`goesNext`)

## How it works

1. **Create the element:** a `Paragraph`, a `Table` (each `addCell` creates a `Cell`) or a `Div`. It's passed to `tryToFit` as an `IBlockElement`.
2. **Create the renderer:** `element.createRendererSubTree()` returns the matching renderer (`ParagraphRenderer`, `TableRenderer`, `DivRenderer`), and `setParent(root)` attaches it to the root renderer.
3. **Lay it out:** `renderer.layout(...)` measures the element against the red rectangle and returns a `LayoutResult`.
4. **Check the status:** `FULL`, `PARTIAL` or `NOTHING`. `statusName()` converts it to text for the label.
5. **Draw:** `fitsHere.draw(...)` paints into the red area. If anything is left over, `goesNext.layout(...)` and `goesNext.draw(...)` handle it in the grey area.

`fitOnce` creates one new page per call with `pdf.addNewPage()`, and a `PdfCanvas` draws the grey and red rectangles onto it.

### Why `DemoRootRenderer`?

`DemoRootRenderer` extends `DocumentRenderer` and sets `currentArea`. Without it, `TableRenderer` throws a `NullPointerException` when it calls `isFirstOnRootArea()`, because it follows the parent chain up to a root with no current area. The `Document` is never used for `add()`. It's only passed to `DemoRootRenderer` so the elements get a default font.

## Classes used

| Layer | Classes |
|---|---|
| Kernel | `PdfWriter`, `PdfDocument`, `PdfPage`, `PdfCanvas` |
| Layout model | `Document`, `Paragraph`, `Table`, `Cell`, `Div` (`IElement` / `IBlockElement`) |
| Layout renderers | `IRenderer`, `AbstractRenderer`, `DocumentRenderer` / `RootRenderer`, `CanvasRenderer` |
| Results | `LayoutResult`, `LayoutContext`, `LayoutArea`, `DrawContext` |

## Requirements

- Java (JDK)
- Maven
- iText 7 (`kernel` and `layout`), pulled in through `pom.xml`

## Running

```bash
./run.sh
```

Or build with Maven directly:

```bash
mvn clean package
```

Then open `layout-results.pdf` to see the results.

## Project structure

```
.
├── src/       # Java source (LayoutResultDemo)
├── pom.xml    # Maven build and iText dependencies
└── run.sh     # Build-and-run script
```

