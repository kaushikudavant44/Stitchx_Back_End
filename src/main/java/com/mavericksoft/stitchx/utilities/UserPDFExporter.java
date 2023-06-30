package com.mavericksoft.stitchx.utilities;
import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mavericksoft.stitchx.dto.CustomerDto;
import com.mavericksoft.stitchx.models.GeneratedTokens;
import com.mavericksoft.stitchx.models.PantMeasurement;
import com.mavericksoft.stitchx.models.ShirtMeasurement;
 
 
public class UserPDFExporter {
    private CustomerDto customer;
     
    private List<GeneratedTokens> generatedTokens;
    
    public UserPDFExporter(CustomerDto customer) {
        this.customer = customer;
    }
    
    public UserPDFExporter(List<GeneratedTokens> generatedTokens) {
        this.generatedTokens = generatedTokens;
    }
 
    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(2);
         
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);
    }
     
    private void writeTableData(PdfPTable table, Document document, Font font) {
    	
    	if(customer.getShirtMeasurement().size()!=0 || !customer.getShirtMeasurement().isEmpty()){
            Paragraph p = new Paragraph("SHIRT", font);
            p.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p);   	
        for (ShirtMeasurement measurement : customer.getShirtMeasurement()) {
            table.addCell("Length : "+String.valueOf(measurement.getShirtLambi()));
            table.addCell("Order Number : "+String.valueOf(customer.getId()));
            table.addCell("Sholder : "+String.valueOf(measurement.getSholder()));
            table.addCell("Customer Name : "+customer.getCustomerName());
            table.addCell("Sleevs : "+String.valueOf(measurement.getBahi()));
            table.addCell("Salesman Name : "+customer.getSalesman());
            table.addCell("Chest : "+String.valueOf(measurement.getChest()));
            table.addCell("Delivery In : "+customer.getDeliveryIn());
            table.addCell("Stomache : "+String.valueOf(measurement.getStomache()));
            table.addCell("Shirt Type : "+measurement.getShirtType());
            table.addCell("Neck : "+String.valueOf(measurement.getNeck()));
            table.addCell("Neck Type : "+measurement.getNeckType());
            table.addCell("Seat : "+String.valueOf(measurement.getSeat()));
            table.addCell("Instructions : "+measurement.getOther());
            table.addCell("Trial Date : "+measurement.getTrialDate());
            table.addCell("Delivery Date : "+measurement.getDeliveryDate());
            table.addCell("Trial Date : "+measurement.getTrialDate());
            table.addCell("Image : "+measurement.getUploadDir());
        }
        }
    	if(customer.getPantMeasurement().size()!=0 || !customer.getPantMeasurement().isEmpty()){
    		Paragraph p = new Paragraph("PANT", font);
            p.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p);
    		for (PantMeasurement measurement : customer.getPantMeasurement()) {
                table.addCell("Length : "+String.valueOf(measurement.getPantLambi()));
                table.addCell("Order Number : "+String.valueOf(customer.getId()));
                table.addCell("Waist : "+String.valueOf(measurement.getKambar()));
                table.addCell("Customer Name : "+customer.getCustomerName());
                table.addCell("Seat : "+String.valueOf(measurement.getSeat()));
                table.addCell("Salesman Name : "+customer.getSalesman());
                table.addCell("Thighs : "+String.valueOf(measurement.getThigh()));
                table.addCell("Delivery In : "+customer.getDeliveryIn());
                table.addCell("Knee : "+String.valueOf(measurement.getKnee()));
                table.addCell("");
                table.addCell("Bottom : "+String.valueOf(measurement.getBottom()));
                table.addCell("");
                table.addCell("Latak : "+String.valueOf(measurement.getLatak()));
                table.addCell("Instructions : "+measurement.getOther());
                table.addCell("Trial Date : "+measurement.getTrialDate());
                table.addCell("Delivery Date : "+measurement.getDeliveryDate());
                table.addCell("Image : "+measurement.getUploadDir());
            }
    		
    	}
    }
     
    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
         
        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);
         
         
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {3.5f, 3.5f});
        table.setSpacingBefore(10);
        table.deleteBodyRows();
         
        //writeTableHeader(table);
        writeTableData(table,document,font);
         
        document.add(table);
         
        document.close();
         
    }
}
