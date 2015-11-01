package com.butlerhq.mds2mms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static javax.xml.transform.OutputKeys.DOCTYPE_PUBLIC;
import static javax.xml.transform.OutputKeys.INDENT;

public class App {
    private static final double BASELINE = 2400;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar mds2mms.jar project_directory");
            return;
        }
        File projectDir = new File(args[0]);
        if (!projectDir.exists()) {
            System.out.println("The specified project directory cannot be found.");
            return;
        }
        File[] mdsFiles = projectDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.startsWith("_") && name.endsWith(".mds");
            }
        });
        if (mdsFiles.length != 1) {
            System.out.println("The project directory must contain one mds file.");
            return;
        }
        File mmsFile = new File(projectDir, mdsFiles[0].getName().replaceFirst(".mds$", ".mms"));
        if (!mdsFiles[0].renameTo(mmsFile)) {
            System.out.println("Could not change extension to mms.");
        }
        File objectDir = new File(projectDir, "objects");
        if (!objectDir.exists()) {
            System.out.println("There is no objects directory, nothing else to do.");
            return;
        }
        File[] srwFiles = objectDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".srw");
            }
        });
        if (srwFiles.length == 0) {
            System.out.println("There are no srw files, nothing else to do.");
            return;
        }
        double scale = getScale(mmsFile);
        if (scale == 1.0) {
            System.out.println("The scale is 1.0 for this project, nothing else to do.");
            return;
        }
        for (File srwFile : srwFiles) {
            updateSRWFile(srwFile, scale);
        }
    }

    private static double getScale(File mmsFile) {
        try {
            ZipInputStream in = new ZipInputStream(new FileInputStream(mmsFile));
            try {
                in.getNextEntry();
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
                int height = parseInt(doc.getDocumentElement().getElementsByTagName("maxOutputHeight").item(0).getTextContent());
                return BASELINE / height;
            } catch (IOException e) {
                throw new RuntimeException("Could not read mds file.", e);
            } catch (ParserConfigurationException e) {
                throw new RuntimeException("An internal error has occurred.", e);
            } catch (SAXException e) {
                throw new RuntimeException("Could not parse mds file.", e);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateSRWFile(File srwFile, double scale) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(srwFile);
            NodeList contents = doc.getElementsByTagName("content");
            if (contents.getLength() == 0) {
                return;
            }
            for (int i = 0; i < contents.getLength(); i++) {
                Node content = contents.item(i);
                NamedNodeMap attr = content.getAttributes();
                Node nodeAttr = attr.getNamedItem("fontsize");
                double size = parseDouble(nodeAttr.getTextContent());
                nodeAttr.setTextContent(valueOf(size * scale));
            }
            doc.setXmlStandalone(true);
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(srwFile);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(DOCTYPE_PUBLIC, "yes");
            transformer.setOutputProperty(INDENT, "yes");
            transformer.transform(source, result);
        } catch (IOException e) {
            throw new RuntimeException("Could not read srw file.", e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("An internal error has occurred.", e);
        } catch (SAXException e) {
            throw new RuntimeException("Could not parse srw file.", e);
        } catch (TransformerException e) {
            throw new RuntimeException("An internal error has occurred.", e);
        }
    }
}
