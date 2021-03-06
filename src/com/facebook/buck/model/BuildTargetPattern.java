/*
 * Copyright 2012-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.facebook.buck.model;

import com.google.common.base.Predicate;

/**
 * A pattern that matches one or more build targets.
 */
public interface BuildTargetPattern extends Predicate<BuildTarget> {

  /**
   * Value used in a build file to specify this pattern, devoid of any
   * {@link com.facebook.buck.rules.Cell} prefix.
   */
  String getCellFreeRepresentation();
}
