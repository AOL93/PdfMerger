package sample;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Controller {

    @FXML
    private TableView tableView;

    @FXML
    public void select(ActionEvent actionEvent) {
        ObservableList<File> files = tableView.getItems();

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        for (File file : selectedFiles) {
            files.add(file);
        }

    }

    @FXML
    public void convert(ActionEvent actionEvent) {
        //PDF conversion init
        ObservableList<File> files = tableView.getItems();

        List<PDDocument> pdfs = new ArrayList<PDDocument>();

        PDDocument outputFile = new PDDocument();
        File finalFile = new File("C:\\Users\\Beata\\Desktop\\testdir\\test.pdf");

        PDRectangle pdfTL, pdfTR, pdfBL,pdfBR;
        PDRectangle outputRect;
        COSDictionary dict;
        PDPage outPdfPage;

        int page = 0;


        try {
            for (File file : files) {
                pdfs.add(PDDocument.load(file));
            }

            while(!pdfs.isEmpty()) {
                PDDocument[] pdf = new PDDocument[4];
                int iter = 0;
                //TODO Remake this with all file and ArrayList
                while(iter<pdf.length && !pdfs.isEmpty()) {
                    pdf[iter] = pdfs.remove(0);
                    iter++;
                }

                /*PDDocument pdf1 = pdfs.remove(0);
                PDDocument pdf2 = !pdfs.isEmpty() ? pdfs.remove(0) : null;
                PDDocument pdf3 = !pdfs.isEmpty() ? pdfs.remove(0) : null;
                PDDocument pdf4 = !pdfs.isEmpty() ? pdfs.remove(0) : null;*/

                pdfTL = pdf[0].getPage(0).getCropBox();

                outputRect = new PDRectangle(2*pdfTL.getWidth(),2*pdfTL.getHeight());

                dict = new COSDictionary();
                dict.setItem(COSName.TYPE, COSName.PAGE);
                dict.setItem(COSName.MEDIA_BOX, outputRect);
                dict.setItem(COSName.CROP_BOX, outputRect);
                dict.setItem(COSName.ART_BOX, outputRect);
                outPdfPage = new PDPage(dict);
                outputFile.addPage(outPdfPage);

                LayerUtility layerUtility = new LayerUtility(outputFile);

                PDFormXObject[] formPDF = new PDFormXObject[4];
                iter = 0;

                for (int i=0;i<pdf.length;i++) {
                    if(pdf[i] == null) break;
                    formPDF[i] = layerUtility.importPageAsForm(pdf[i],0);
                }


                /*PDFormXObject formPdf1 = layerUtility.importPageAsForm(pdf1,0);
                PDFormXObject formPdf2 = layerUtility.importPageAsForm(pdf2,0);
                PDFormXObject formPdf3 = layerUtility.importPageAsForm(pdf3,0);
                PDFormXObject formPdf4 = layerUtility.importPageAsForm(pdf4,0);*/

                AffineTransform afTopLeft = new AffineTransform();
//                layerUtility.appendFormAsLayer(outPdfPage,formPdf1,afTopLeft,"topLeft" + page);

                AffineTransform afTopRight = AffineTransform.getTranslateInstance(pdfTL.getWidth(),0.0);
//                layerUtility.appendFormAsLayer(outPdfPage,formPdf2,afTopRight,"topRight" + page);

                AffineTransform afBottomLeft = AffineTransform.getTranslateInstance(0.0,pdfTL.getHeight());
//                layerUtility.appendFormAsLayer(outPdfPage,formPdf3,afBottomLeft,"bottomLeft" + page);

                AffineTransform afBottomRight = AffineTransform.getTranslateInstance(pdfTL.getWidth(),pdfTL.getHeight());
//                layerUtility.appendFormAsLayer(outPdfPage,formPdf4,afBottomRight,"bottomRight" + page++);

                for(int i=0;i<formPDF.length;i++) {
                    if (formPDF[i]==null) break;

                    switch(i) {
                        case 0:
                            layerUtility.appendFormAsLayer(outPdfPage,formPDF[i],afTopLeft,"topLeft" + page);
                            break;
                        case 1:
                            layerUtility.appendFormAsLayer(outPdfPage,formPDF[i],afTopRight,"topRight" + page);
                            break;
                        case 2:
                            layerUtility.appendFormAsLayer(outPdfPage,formPDF[i],afBottomLeft,"bottomLeft" + page);
                            break;
                        case 3:
                            layerUtility.appendFormAsLayer(outPdfPage,formPDF[i],afBottomRight,"bottomRight" + page++);
                            break;
                    }
                }

                outputFile.save(finalFile);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Files merged.", ButtonType.OK);
            alert.setHeaderText("ALL DONE");
            alert.show();

        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
