package org.pdxfinder.dataexport;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.pdxfinder.BaseTest;
import org.pdxfinder.utils.CbpTransformer;
import org.pdxfinder.utils.CbpTransformer.cbioType;

import java.io.*;


public class CbpTransformerTests extends BaseTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private CbpTransformer cbpTransformer = new CbpTransformer();

    private File jsonDummy;
    private File exportFolder;
    private File templatesFolder;
    private cbioType mutDataType;
    private cbioType gisticDataType;

    @Before
    public void init() throws IOException {
        jsonDummy = folder.newFile("UtilityTest.json");
        mutDataType = cbioType.MUT;
        gisticDataType = cbioType.GISTIC;

        TemporaryFolder rootFolder = new TemporaryFolder();
        rootFolder.create();
        exportFolder = rootFolder.newFolder();
        templatesFolder = rootFolder.newFolder();
        createMockTemplate();
    }

    public void createMockTemplate() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet omic = workbook.createSheet("Test");
        Row headers = omic.createRow(0);
        for(int i = 0; i < 25; i++){
            headers.createCell(i).setCellValue(i );
        }

        FileOutputStream out = new FileOutputStream(new File(templatesFolder.getAbsoluteFile() + "/mutation_template.xlsx"));
        workbook.write(out);
        out.close();

        FileOutputStream cnaOut = new FileOutputStream(new File(templatesFolder.getAbsoluteFile() + "/cna_template.xlsx"));
        workbook.write(cnaOut);
        out.close();
    }

    @Test(expected = IOException.class)
    public void Given_nonExistentJsonFilesArePassed_WhenExportCBPisCalled_Then_throwIOexception() throws IOException {
        cbpTransformer.exportCBP(exportFolder, templatesFolder, new File("/tmp/not/existing"), mutDataType);
    }

    @Test(expected = IOException.class)
    public void Given_nonExistentTemplateDirectoryisPassed_WhenExportCBPisCalled_Then_throwIOexception() throws IOException {
        cbpTransformer.exportCBP(exportFolder, new File("/Fake/Path/"), jsonDummy, mutDataType);
    }

    @Test
    public void Give_JsonArrayAndValidImportDirectory_When_exportsIsCalled__ThenNewMutDirExists() throws IOException {
        String ns = "Not Specified";

        BufferedWriter writer = new BufferedWriter(new FileWriter(jsonDummy));
        writer.write("[ { \"patientId\":\"1\", \"sampleId\":\"2\", \"chr\":\"3\", \"startPosition\":\"4\", \"referenceAllele\":\"5\", \"variantAllele\":\"6\", \"ncbiBuild\":\"7\"} ] ");
        writer.close();

        cbpTransformer.exportCBP(exportFolder, templatesFolder, jsonDummy, mutDataType);

        File actualGroupFile = new File(exportFolder.getAbsoluteFile() + "/UtilityTest.json");
        File actualMutDir = new File(actualGroupFile + "/mut");
        File outputData = new File(actualMutDir + "/" + jsonDummy.getName() + "_mut.tsv");

        Assert.assertTrue(actualGroupFile.exists());
        Assert.assertTrue(actualMutDir.exists());
        Assert.assertTrue(outputData.exists());
    }

    @Test
    public void Give_JsonArrayAndValidImportDirectory_When_exportsIsCalled__ThenNewMutTsvExistsWithAppropriateData() throws IOException {
        String ns = "Not Specified";

        BufferedWriter writer = new BufferedWriter(new FileWriter(jsonDummy));
        writer.write("[ { \"patientId\":\"1\", \"sampleId\":\"2\", \"chr\":\"3\", \"startPosition\":\"4\", \"referenceAllele\":\"5\", \"variantAllele\":\"6\", \"ncbiBuild\":\"7\"} ] ");
        writer.close();

        cbpTransformer.exportCBP(exportFolder, templatesFolder, jsonDummy, mutDataType);

        File actualGroupFile = new File(exportFolder.getAbsoluteFile() + "/UtilityTest.json");
        File actualMutDir = new File(actualGroupFile + "/mut");
        File outputData = new File(actualMutDir + "/" + jsonDummy.getName() + "_mut.tsv");

        Assert.assertTrue(outputData.exists());

        BufferedReader reader = new BufferedReader(new FileReader(outputData.toString()));
        reader.readLine();
        String[] header = reader.readLine().split("\t");

        Assert.assertEquals("1", header[0]);
        Assert.assertEquals("2", header[1]);
        Assert.assertEquals("3", header[15]);
        Assert.assertEquals("4", header[16]);
        Assert.assertEquals("5",header[17]);
        Assert.assertEquals("6",header[18]);
        Assert.assertEquals("7", header[25]);

    }

    @Test
    public void Give_GisticData_When_exportsIsCalled__ThenGisticIsCreated() throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(jsonDummy));
        writer.write("[ { \"patientId\":\"1\", \"sampleId\":\"2\", \"entrezGeneId\":\"3\", \"alteration\":\"4\"} ] ");
        writer.close();


        cbpTransformer.exportCBP(exportFolder, templatesFolder, jsonDummy, gisticDataType);

        File actualGroupFile = new File(exportFolder.getAbsoluteFile() + "/UtilityTest.json");
        File actualMutDir = new File(actualGroupFile + "/cna");
        File outputData = new File(actualMutDir + "/" + jsonDummy.getName() + "_cna.tsv");

        Assert.assertTrue(outputData.exists());

        BufferedReader reader = new BufferedReader(new FileReader(outputData.toString()));
        reader.readLine();
        String[] actualRow = reader.readLine().split("\t");

        Assert.assertEquals( 17, actualRow.length);
        Assert.assertEquals( "1",actualRow[0]);
        Assert.assertEquals( "2", actualRow[1]);
        Assert.assertEquals( "3", actualRow[9]);
        Assert.assertEquals("4",actualRow[16]);
    }
}


