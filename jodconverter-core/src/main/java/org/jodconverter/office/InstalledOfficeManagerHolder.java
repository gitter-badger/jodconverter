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

package org.jodconverter.office;

import org.jodconverter.DefaultConverter;

/**
 * Holds a unique instance of an {@link OfficeManager} that will be used by created {@link
 * DefaultConverter} when no office manager are given to the converter builder.
 */
public final class InstalledOfficeManagerHolder {

  private static OfficeManager instance;

  /**
   * Gets the static instance of the static holder class.
   *
   * @return The main default office manager.
   */
  public static synchronized OfficeManager getInstance() {

    return instance;
  }

  /**
   * Sets the static instance of the static holder class.
   *
   * @param manager The main default office manager.
   * @return the previous installed office manager, or {@code null} if there was no installed office
   *     manager.
   */
  public static synchronized OfficeManager setInstance(final OfficeManager manager) {

    final OfficeManager oldManager = instance;
    instance = manager;
    return oldManager;
  }

  // Suppresses default constructor, ensuring non-instantiability.
  private InstalledOfficeManagerHolder() {
    throw new AssertionError("Utility class must not be instantiated");
  }
}
