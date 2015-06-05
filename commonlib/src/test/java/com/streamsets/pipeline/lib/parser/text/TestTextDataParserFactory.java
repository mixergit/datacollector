/**
 * (c) 2014 StreamSets, Inc. All rights reserved. May not
 * be copied, modified, or distributed in whole or part without
 * written consent of StreamSets, Inc.
 */
package com.streamsets.pipeline.lib.parser.text;

import com.streamsets.pipeline.api.OnRecordError;
import com.streamsets.pipeline.api.Record;
import com.streamsets.pipeline.api.Stage;
import com.streamsets.pipeline.lib.data.DataFactory;
import com.streamsets.pipeline.lib.parser.DataParser;
import com.streamsets.pipeline.lib.parser.DataParserFactoryBuilder;
import com.streamsets.pipeline.lib.parser.DataParserFormat;
import com.streamsets.pipeline.sdk.ContextInfoCreator;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;

public class TestTextDataParserFactory {

  private Stage.Context getContext() {
    return ContextInfoCreator.createSourceContext("i", false, OnRecordError.TO_ERROR, Collections.EMPTY_LIST);
  }

  @Test
  public void testGetParserString() throws Exception {

    DataParserFactoryBuilder dataParserFactoryBuilder = new DataParserFactoryBuilder(getContext(), DataParserFormat.TEXT);
    DataFactory dataFactory = dataParserFactoryBuilder
      .setMaxDataLen(3)
      .build();
    Assert.assertTrue(dataFactory instanceof TextDataParserFactory);
    TextDataParserFactory factory = (TextDataParserFactory) dataFactory;

    DataParser parser = factory.getParser("id", "Hello\n".getBytes());
    Assert.assertEquals(0, Long.parseLong(parser.getOffset()));
    Record record = parser.parse();
    Assert.assertTrue(record.has("/text"));
    Assert.assertTrue(record.has("/truncated"));
    Assert.assertEquals(6, Long.parseLong(parser.getOffset()));
    parser.close();
  }

  @Test
  public void testGetParserReader() throws Exception {
    DataParserFactoryBuilder dataParserFactoryBuilder = new DataParserFactoryBuilder(getContext(), DataParserFormat.TEXT);
    DataFactory dataFactory = dataParserFactoryBuilder
      .setMaxDataLen(3)
      .build();
    Assert.assertTrue(dataFactory instanceof TextDataParserFactory);
    TextDataParserFactory factory = (TextDataParserFactory) dataFactory;
    InputStream is = new ByteArrayInputStream("Hello\nBye".getBytes());
    DataParser parser = factory.getParser("id", is, 0);
    Assert.assertEquals(0, Long.parseLong(parser.getOffset()));
    Record record = parser.parse();
    Assert.assertTrue(record.has("/text"));
    Assert.assertTrue(record.has("/truncated"));
    Assert.assertEquals(6, Long.parseLong(parser.getOffset()));
    parser.close();
  }

  @Test
  public void testGetParserReaderWithOffset() throws Exception {
    DataParserFactoryBuilder dataParserFactoryBuilder = new DataParserFactoryBuilder(getContext(), DataParserFormat.TEXT);
    DataFactory dataFactory = dataParserFactoryBuilder
      .setMaxDataLen(1000)
      .build();
    Assert.assertTrue(dataFactory instanceof TextDataParserFactory);
    TextDataParserFactory factory = (TextDataParserFactory) dataFactory;
    InputStream is = new ByteArrayInputStream("Hello\nBye".getBytes());
    DataParser parser = factory.getParser("id", is, 6);
    Assert.assertEquals(6, Long.parseLong(parser.getOffset()));
    Record record = parser.parse();
    Assert.assertTrue(record.has("/text"));
    Assert.assertEquals(9, Long.parseLong(parser.getOffset()));
    parser.close();
  }

  @Test
  public void testCharacterBaseParserMethod() throws Exception {
    DataParserFactoryBuilder dataParserFactoryBuilder = new DataParserFactoryBuilder(getContext(), DataParserFormat.TEXT);
    DataFactory dataFactory = dataParserFactoryBuilder
        .setMaxDataLen(1000)
        .build();
    Assert.assertTrue(dataFactory instanceof TextDataParserFactory);
    TextDataParserFactory factory = (TextDataParserFactory) dataFactory;
    DataParser parser = factory.getParser("id", "Hello\nBye");
    Assert.assertEquals(0, Long.parseLong(parser.getOffset()));
    Record record = parser.parse();
    Assert.assertTrue(record.has("/text"));
    Assert.assertEquals(6, Long.parseLong(parser.getOffset()));
    parser.close();
  }

  /*@Test
  public void testTruncateWithFile() throws Exception {
    File testDir = new File("target", UUID.randomUUID().toString());
    Assert.assertTrue(testDir.mkdirs());
    File testFile = new File(testDir, "test.txt");
    Writer writer = new FileWriter(testFile);
    IOUtils.write("HelloHello\r\n\r\n", writer);
    writer.close();
    DataParserFactoryBuilder dataParserFactoryBuilder = new DataParserFactoryBuilder(getContext(), DataParserFormat.TEXT);
    DataFactory dataFactory = dataParserFactoryBuilder
      .setMaxDataLen(3)
      .setCharset(Charset.forName("UTF-8"))
      .setOverRunLimit(100000)
      .build();
    Assert.assertTrue(dataFactory instanceof TextCharDataParserFactory);
    TextCharDataParserFactory factory = (TextCharDataParserFactory) dataFactory;

    DataParser parser = factory.getParser(testFile, 0);
    Record record = parser.parse();
    Assert.assertTrue(record.has("/text"));
    Assert.assertTrue(record.has("/truncated"));
    parser.close();
  }*/

}
