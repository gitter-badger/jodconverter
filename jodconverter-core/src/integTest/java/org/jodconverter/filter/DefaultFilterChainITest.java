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

package org.jodconverter.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.jodconverter.AbstractOfficeITest;
import org.jodconverter.filter.text.PageCounterFilter;
import org.jodconverter.filter.text.PageSelectorFilter;

public class DefaultFilterChainITest extends AbstractOfficeITest {

  private static final String SOURCE_FILENAME = "test_multi_page.doc";
  private static final File SOURCE_FILE = new File(DOCUMENTS_DIR, SOURCE_FILENAME);

  private static File outputDir;

  /** Creates an output test directory just once. */
  @BeforeClass
  public static void setUpClass() {

    outputDir = new File(TEST_OUTPUT_DIR, DefaultFilterChainITest.class.getSimpleName());
    outputDir.mkdirs();
  }

  /** Deletes the output test directory once the tests are all done. */
  @AfterClass
  public static void tearDownClass() {

    FileUtils.deleteQuietly(outputDir);
  }

  /**
   * Test that reseting a chain will actually allow us to reuse it.
   *
   * @throws Exception if an error occurs.
   */
  @Test
  public void reset_WithPageCounterAndSelector_ShoudCountProperSizesForBothUsage()
      throws Exception {

    final File targetFile1 = new File(outputDir, SOURCE_FILENAME + ".page1.txt");
    final File targetFile2 = new File(outputDir, SOURCE_FILENAME + ".page1again.txt");

    final PageCounterFilter countFilter1 = new PageCounterFilter();
    final PageSelectorFilter selectorFilter = new PageSelectorFilter(1);
    final PageCounterFilter countFilter2 = new PageCounterFilter();

    // Test the filters
    final DefaultFilterChain chain =
        new DefaultFilterChain(countFilter1, selectorFilter, countFilter2, RefreshFilter.REFRESH);
    converter.convert(SOURCE_FILE).filterWith(chain).to(targetFile1).execute();

    final String content = FileUtils.readFileToString(targetFile1, Charset.forName("UTF-8"));
    assertThat(content)
        .contains("Test document Page 1")
        .doesNotContain("Test document Page 2")
        .doesNotContain("Test document Page 3");
    assertThat(countFilter1.getPageCount()).isEqualTo(3);
    assertThat(countFilter2.getPageCount()).isEqualTo(1);

    // Reset the chain and test the filters again
    chain.reset();
    converter.convert(targetFile1).filterWith(chain).to(targetFile2).execute();
    assertThat(countFilter1.getPageCount()).isEqualTo(1);
    assertThat(countFilter2.getPageCount()).isEqualTo(1);
  }
}
