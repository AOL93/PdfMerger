package sample;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

    private File finalFile;

    @FXML
    private TableView tableView;

    @FXML
    private TextField destPath;

    @FXML
    public void initialize() {
        String dir = System.getProperty("user.dir") + "\\result.pdf";
        finalFile = new File(dir);
        destPath.setText(finalFile.getPath());
    }

    @FXML
    public void select(ActionEvent actionEvent) {
        ObservableList<Pdf> files = tableView.getItems();

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if(selectedFiles != null) {
            for (File file : selectedFiles) {
                files.add(new Pdf(file));
            }
        }

    }

    @FXML
    public void convert(ActionEvent actionEvent) {
        //PDF conversion init
        ObservableList<Pdf> files = tableView.getItems();

        List<PDDocument> pdfs = new ArrayList<PDDocument>();

        PDDocument outputFile = new PDDocument();

        PDRectangle pdfTL;
        PDRectangle outputRect;
        COSDictionary dict;
        PDPage outPdfPage;

        int page = 0;


        try {
            for (Pdf file : files) {
                try {
                    pdfs.add(PDDocument.load(file));
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText("An error occurred during conversion. Check selected files");
                    alert.show();
                    return;
                }
            }

            while(!pdfs.isEmpty()) {
                PDDocument[] pdf = new PDDocument[4];
                int iter = 0;

                //TODO Remake this with all file and ArrayList
                while(iter<pdf.length && !pdfs.isEmpty()) {
                    pdf[iter] = pdfs.remove(0);

                    iter++;
                }

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

                    pdf[i].close();
                }

                AffineTransform afTopLeft = new AffineTransform();
                AffineTransform afTopRight = AffineTransform.getTranslateInstance(pdfTL.getWidth(),0.0);
                AffineTransform afBottomLeft = AffineTransform.getTranslateInstance(0.0,pdfTL.getHeight());
                AffineTransform afBottomRight = AffineTransform.getTranslateInstance(pdfTL.getWidth(),pdfTL.getHeight());

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

            outputFile.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Files merged.", ButtonType.OK);
            alert.setHeaderText("ALL DONE");
            alert.show();

        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void selectPath(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));

        finalFile = fileChooser.showSaveDialog(null);
        if(finalFile != null) {
            destPath.setText(finalFile.getPath());
        }
    }

    @FXML
    public void clearList(ActionEvent actionEvent) {
        ObservableList<Pdf> list = tableView.getItems();
        list.clear();
    }
}
