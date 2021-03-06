/*
 * Copyright 2004 - 2012 Mirko Nasato and contributors
 *           2016 - 2017 Simon Braconnier and contributors
 *
 * This file is part of JODConverter - Java OpenDocument Converter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jodconverter.cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.jodconverter.cli.util.ExitException;
import org.jodconverter.cli.util.NoExitSecurityManager;
import org.jodconverter.cli.util.SystemLogHandler;
import org.jodconverter.office.OfficeUtils;

/**
 * This class tests the {@link Convert} class, which contains the main function of the cli module.
 */
public class ConvertITest {

  private static final String TEST_OUTPUT_DIR = "build/integTest-results/";
  private static final String CONFIG_DIR = "src/integTest/resources/config/";
  private static final String SOURCE_FILE = "src/integTest/resources/documents/test1.doc";

  private static File outputDir;

  /**
   * Redirects the console output and also changes the security manager so we can trap the exit code
   * of the application.
   */
  @BeforeClass
  public static void setUpClass() {

    outputDir = new File(TEST_OUTPUT_DIR, ConvertITest.class.getSimpleName());
    outputDir.mkdirs();

    // Don't allow the program to exit the VM
    System.setSecurityManager(new NoExitSecurityManager());
  }

  /** Resets the security manager and deletes the output directory once the tests are all done. */
  @AfterClass
  public static void tearDownClass() {

    // Delete the output directory
    FileUtils.deleteQuietly(outputDir);

    // Restore security manager
    System.setSecurityManager(null);
  }

  @Test
  public void convert_WithFilenames_ShouldSucceed() throws Exception {

    final File inputFile = new File(SOURCE_FILE);
    final File outputFile = new File(outputDir, "convert.pdf");

    assertThat(outputFile).doesNotExist();

    try {
      SystemLogHandler.startCapture();
      Convert.main(new String[] {inputFile.getPath(), outputFile.getPath()});

      // Be sure the ExitException exception is thrown.
      fail();

    } catch (Exception ex) {
      SystemLogHandler.stopCapture();
      assertThat(ex)
          .isExactlyInstanceOf(ExitException.class)
          .hasFieldOrPropertyWithValue("status", 0);

      assertThat(outputFile).isFile();
      assertThat(outputFile.length()).isGreaterThan(0L);
    }
  }

  @Test
  public void convert_WithOutputFormat_ShouldSucceed() throws Exception {

    final File inputFile = new File(SOURCE_FILE);
    final File outputFile =
        new File(
            inputFile.getParentFile(), FilenameUtils.getBaseName(inputFile.getName()) + ".pdf");

    assertThat(outputFile).doesNotExist();

    try {
      SystemLogHandler.startCapture();
      Convert.main(new String[] {"-f", "pdf", inputFile.getPath()});

      // Be sure the ExitException exception is thrown.
      fail();

    } catch (Exception ex) {
      SystemLogHandler.stopCapture();
      assertThat(ex)
          .isExactlyInstanceOf(ExitException.class)
          .hasFieldOrPropertyWithValue("status", 0);

      assertThat(outputFile).isFile();
      assertThat(outputFile.length()).isGreaterThan(0L);

      FileUtils.deleteQuietly(outputFile); // Prevent further test failure.
    }
  }

  @Test
  public void convert_WithMultipleFilters_ShouldSucceed() throws Exception {

    final File filterChainFile = new File(CONFIG_DIR, "applicationContext_multipleFilters.xml");
    final File inputFile = new File(SOURCE_FILE);
    final File outputFile = new File(outputDir, "convert_WithMultipleFilters.pdf");

    assertThat(outputFile).doesNotExist();

    try {
      SystemLogHandler.startCapture();
      Convert.main(
          new String[] {
            "-a", filterChainFile.getPath(), inputFile.getPath(), outputFile.getPath()
          });

      // Be sure the ExitException exception is thrown.
      fail();

    } catch (Exception ex) {
      SystemLogHandler.stopCapture();
      assertThat(ex)
          .isExactlyInstanceOf(ExitException.class)
          .hasFieldOrPropertyWithValue("status", 0);

      assertThat(outputFile).isFile();
      assertThat(outputFile.length()).isGreaterThan(0L);
    }
  }

  @Test
  public void convert_WithSingleFilter_ShouldSucceed() throws Exception {

    final File filterChainFile = new File(CONFIG_DIR, "applicationContext_singleFilter.xml");
    final File inputFile = new File(SOURCE_FILE);
    final File outputFile = new File(outputDir, "convert_WithSingleFilter.pdf");

    assertThat(outputFile).doesNotExist();

    try {
      SystemLogHandler.startCapture();
      Convert.main(
          new String[] {
            "-a", filterChainFile.getPath(), inputFile.getPath(), outputFile.getPath()
          });

      // Be sure the ExitException exception is thrown.
      fail();

    } catch (Exception ex) {
      SystemLogHandler.stopCapture();
      assertThat(ex)
          .isExactlyInstanceOf(ExitException.class)
          .hasFieldOrPropertyWithValue("status", 0);

      assertThat(outputFile).isFile();
      assertThat(outputFile.length()).isGreaterThan(0L);
    }
  }

  @Test
  public void main_WithAllCustomizableOption_ExecuteAndExitWithCod0() throws Exception {

    try {
      Convert.main(
          new String[] {
            "-i", OfficeUtils.getDefaultOfficeHome().getPath(),
            "-m", OfficeUtils.findBestProcessManager().getClass().getName(),
            "-t", "30000",
            "-p", "2002",
            "input1.txt", "output1.pdf"
          });

      // Be sure an exception is thrown.
      fail();

    } catch (Exception ex) {
      SystemLogHandler.stopCapture();
      assertThat(ex)
          .isExactlyInstanceOf(ExitException.class)
          .hasFieldOrPropertyWithValue("status", 0);
    }
  }
}
