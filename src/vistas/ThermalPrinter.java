package vistas;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;


public class ThermalPrinter {

    private static final String PDF_FILE = "customer_invoice.pdf";
    private static final String COMMISSION_PDF_FILE = "customer_invoice.pdf";
    private static final float PAGE_WIDTH = 80 * 2.83f; // 80 mm de ancho para papel de 8 cm, convertido a puntos

    public String createPDF(String clientInfo, List<String[]> productList, String nombreCliente) {
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm a");
        LocalDate dateNow = LocalDate.now();
        LocalDateTime dateTimeNow = LocalDateTime.now();

        String monthDirectory = "facturas/" + dateNow.format(monthFormat);
        String dayDirectory = monthDirectory + "/" + dateNow.format(dayFormat);
        String nowFormatted = dateTimeNow.format(dateTimeFormat);

        try {
            Files.createDirectories(Paths.get(monthDirectory));
            Files.createDirectories(Paths.get(dayDirectory));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String outputFile = dayDirectory + "/" + nombreCliente + "_" + nowFormatted + ".pdf";

        String nitHotel = "Nit. 1085324860-9";
        String hotelAddress = "Dirección: Calle 18 N 16-10 Centro-Pasto";
        String hotelPhone = "Teléfono: (602)7418969 - (+57)3145519811";
        String hotelEmail = "Email:";
        String thankYouMessage = """
                                 Muchas Gracias!
                                 Por darnos la oportunidad de brindarle nuestro 
                                 servicio de alojamiento, esperamos que sea de 
                                 su agrado.
                                 """;
        String aviso = """
                       Recuerda que el check_out se debe hacer antes de la una de la tarde
                       en tu fecha de salida, el exeder este limite de tiempo en la permanencia 
                       puede ocasionar cobros adicionales al valor de esta factura.                       
                       """;

        try {
            PdfWriter writer = new PdfWriter(outputFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, new PageSize(PAGE_WIDTH, PageSize.A4.getHeight()));
            document.setMargins(130, 20, 20, 10);

            // Cargar el QR
            InputStream qrStream = getClass().getResourceAsStream("/img/QR_Reservas.png");
            ImageData qrCodeImageData = ImageDataFactory.create(IOUtils.toByteArray(qrStream));

            float qrCodeWidth = 130;
            float qrCodeHeight = qrCodeImageData.getHeight() * (qrCodeWidth / qrCodeImageData.getWidth());
            float qrCodeXPos = (float) ((PAGE_WIDTH - qrCodeWidth) / 2.5);
            float marginBottom = 20;
            float qrCodeYPos = marginBottom;
            Image qrCodeImage = new Image(qrCodeImageData).setWidth(qrCodeWidth).setHeight(qrCodeHeight).setFixedPosition(qrCodeXPos, qrCodeYPos);

            // Cargar el logo
            InputStream logoStream = getClass().getResourceAsStream("/img/Imagen2.png");
            ImageData imageData = ImageDataFactory.create(IOUtils.toByteArray(logoStream));

            float imageWidth = 120;
            float imageHeight = imageData.getHeight() * (imageWidth / imageData.getWidth());
            float xPos = (float) ((PAGE_WIDTH - imageWidth) / 2.5);
            float marginTop = 20;
            float yPos = PageSize.A4.getHeight() - marginTop - imageHeight;

            Image logo = new Image(imageData).setWidth(imageWidth).setHeight(imageHeight).setFixedPosition(xPos, yPos);

            document.add(logo);

            Paragraph nitHotelParagraph = new Paragraph(nitHotel)
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER);
            nitHotelParagraph.setMultipliedLeading(0.1f);
            document.add(nitHotelParagraph);

            Paragraph hotelAdresParagraph = new Paragraph(hotelAddress)
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER);
            hotelAdresParagraph.setMultipliedLeading(0.1f);
            document.add(hotelAdresParagraph);

            Paragraph hotelPhoneParagraph = new Paragraph(hotelPhone).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(8).setTextAlignment(TextAlignment.CENTER);
            hotelPhoneParagraph.setMultipliedLeading(0.1f);
            document.add(hotelPhoneParagraph);

            Paragraph emailHotelParagraph = (new Paragraph(hotelEmail).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(8).setTextAlignment(TextAlignment.CENTER));
            emailHotelParagraph.setMultipliedLeading(0.1f);
            document.add(emailHotelParagraph);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Información del cliente").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12).setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph(clientInfo).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(10).setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("------------------------------------------").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(thankYouMessage).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(8).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("------------------------------------------").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("""
                                       Si desea reservar una habitación o necesita transportarse al aeropuerto, puede hacerlo desde aquí.                                       
                                       """)
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(10).setTextAlignment(TextAlignment.CENTER).setMultipliedLeading(1.0f));
            document.add(new Paragraph("WhatsApp (+57)3232951780").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(10).setTextAlignment(TextAlignment.CENTER).setMultipliedLeading(1.0f));
            float adjustment = 120;
            qrCodeYPos += adjustment;
            qrCodeImage = new Image(qrCodeImageData).setWidth(qrCodeWidth).setHeight(qrCodeHeight).setFixedPosition(qrCodeXPos, qrCodeYPos);
            document.add(qrCodeImage);
            document.add(new Paragraph("\n\n\n\n\n\n\n\n"));
            document.add(new Paragraph(aviso).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA)).setFontSize(6).setTextAlignment(TextAlignment.CENTER));
            document.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }

    public void printCommissionPDF() {
        // Impresión del PDF de la boleta de comisión
        try {
            PDDocument pdfDocument = PDDocument.load(new File(COMMISSION_PDF_FILE));

            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
            if (printService == null) {
                System.err.println("No se encontró ninguna impresora predeterminada");
                return;
            }

            DocPrintJob printJob = printService.createPrintJob();
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(new MediaPrintableArea(0, 0, PAGE_WIDTH / 2.83f, PageSize.A4.getHeight() / 2.83f, MediaPrintableArea.MM));

            Doc doc = new SimpleDoc(new PDFPageable(pdfDocument), DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
            printJob.print(doc, printRequestAttributeSet);
            pdfDocument.close();
        } catch (IOException | PrintException e) {
            e.printStackTrace();
        }
    }

    public void printPDF(String filName) {
        try {
            
            PDDocument pdfDocument = PDDocument.load(new File(filName));

            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
            if (printService == null) {
                System.err.println("No se encontró ninguna impresora predeterminada");
                return;
            }

            DocPrintJob printJob = printService.createPrintJob();
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(new MediaPrintableArea(0, 0, PAGE_WIDTH / 2.83f, PageSize.A4.getHeight() / 2.83f, MediaPrintableArea.MM));

            Doc doc = new SimpleDoc(new PDFPageable(pdfDocument), DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
            printJob.print(doc, printRequestAttributeSet);
            pdfDocument.close();
        } catch (IOException | PrintException e) {
            e.printStackTrace();
        }
    }

    public void imprimirComision(String nomUsu, String nombreComision, String commissionistPhone, String commissionValue, String fecha, String hora) {
        String mensajeComision = """
                                 Muchas Gracias Por Tu Apoyo!                                 
                                 """;
        // AQUI CREAMOS LA BOLETA SI HAY COMISION
        try {
            PdfWriter writer = new PdfWriter(COMMISSION_PDF_FILE);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, new PageSize(PAGE_WIDTH, PageSize.A4.getHeight()));
            document.setMargins(20, 20, 60, 10);

            //AQUI SE CONFIGURA LA INFORMACION DEL PDF
            document.add(new Paragraph("GESTION DE APOYOS").setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18).setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("Fecha: " + fecha + "          Hora: " + hora).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(10).setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Comisionista:__   " + "     Taxista:__"));
            document.add(new Paragraph("Transportador:__  " + "    Agencia:__"));
            document.add(new Paragraph("-----------------------------------------------"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(nomUsu + nombreComision));
            document.add(new Paragraph("Teléfono: " + commissionistPhone));
            document.add(new Paragraph("Valor de comisión: " + "$" + commissionValue));
            document.add(new Paragraph("Firma: " + "______________________"));
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph(mensajeComision).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(12).setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("\n\n"));
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
