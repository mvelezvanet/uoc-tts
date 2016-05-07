package edu.uoc.reader;

import org.apache.xerces.dom.TextImpl;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.draw.DrawObjectElement;
import org.odftoolkit.odfdom.dom.element.text.TextSequenceDeclElement;
import org.odftoolkit.odfdom.dom.element.text.TextSoftPageBreakElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by mvelezm on 14/04/16.
 */
public class ODPParser extends ODFParser {
    private String documentName;
    private OdfDocument odfDocument;
    private String text;
    private String[] textSplitted;

    public String[] getTextSplitted() {
        return textSplitted;
    }

    public void setTextSplitted(String[] textSplitted) {
        this.textSplitted = textSplitted;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private static final String PAGE_BREAK_MARK = "PAGE-BREAK-MARK";

    @Override
    public String toString() {

        return "ODFParser{" +
                "documentName='" + documentName + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public ODPParser(OdfDocument document) throws Exception {
        OdfContentDom content = null;

        content = document.getContentDom();
        NodeList list = content.getRootElement().getElementsByTagName("office:text");
        String lastStyle = "";

        for (int j = 0; j<list.item(0).getChildNodes().getLength(); j++){
            Node singleNode = list.item(0).getChildNodes().item(j);
            if ( singleNode instanceof OdfTextParagraph){
                if (lastStyle.equals("")){
                    //First TextParagraph
                    lastStyle = ((OdfTextParagraph) singleNode).getStyleName();
                }
                else if(!lastStyle.equals(((OdfTextParagraph) singleNode).getStyleName()))
                    text = text + PAGE_BREAK_MARK;
            }

            text = text + this.recurse(list.item(0).getChildNodes().item(j));
        }

        System.out.println("#########################################################################################");
        System.out.println(text);
        System.out.println("#########################################################################################");

        textSplitted = this.text.replaceAll("(.{0,"+ 100+"})\\b", "$1\n").split("\n");
    }

    public String recurse(Node node) {
        String innerText = "";
        final String ENDLN = "\n";

        if (node instanceof TextImpl) {
            innerText = innerText + node.getTextContent() + ENDLN;
        }

        else if(node instanceof TextSoftPageBreakElement) {
            innerText = innerText + PAGE_BREAK_MARK;
        }

        if (node.hasChildNodes()) {
            for (int j = 0; j<node.getChildNodes().getLength(); j++){
                innerText = innerText + this.recurse(node.getChildNodes().item(j));
            }
        }
        return innerText;
    }
}