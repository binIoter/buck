/*
 * Copyright 2015-present Facebook, Inc.
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

package com.facebook.buck.cli;

import static org.junit.Assert.assertThat;

import com.facebook.buck.model.BuildTargetFactory;
import com.facebook.buck.testutil.integration.ProjectWorkspace;
import com.facebook.buck.testutil.integration.TemporaryPaths;
import com.facebook.buck.testutil.integration.TestDataHelper;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildCommandIntegrationTest {

  @Rule
  public TemporaryPaths tmp = new TemporaryPaths();

  @Test
  public void justBuild() throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this, "just_build", tmp);
    workspace.setUp();
    workspace.runBuckBuild("--just-build", "//:bar", "//:foo").assertSuccess();
    assertThat(
        workspace.getBuildLog().getAllTargets(),
        Matchers.contains(BuildTargetFactory.newInstance(workspace.getDestPath(), "//:bar")));
  }

  @Test
  public void showOutput() throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this, "just_build", tmp);
    workspace.setUp();
    ProjectWorkspace.ProcessResult runBuckResult = workspace.runBuckBuild(
        "--show-output",
        "//:bar");
    runBuckResult.assertSuccess();
    assertThat(runBuckResult.getStdout(), Matchers.containsString("//:bar buck-out"));
  }

  @Test
  public void showFullOutput() throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this, "just_build", tmp);
    workspace.setUp();
    ProjectWorkspace.ProcessResult runBuckResult = workspace.runBuckBuild(
        "--show-full-output",
        "//:bar");
    runBuckResult.assertSuccess();
    Path expectedRootDirectory = tmp.getRoot();
    String expectedOutputDirectory = expectedRootDirectory.resolve("buck-out/").toString();
    String stdout = runBuckResult.getStdout();
    assertThat(stdout, Matchers.containsString("//:bar "));
    assertThat(stdout, Matchers.containsString(expectedOutputDirectory));
  }

  @Test
  public void showRuleKey() throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this, "just_build", tmp);
    workspace.setUp();
    ProjectWorkspace.ProcessResult runBuckResult = workspace.runBuckBuild(
        "--show-rulekey",
        "//:bar");
    runBuckResult.assertSuccess();

    Pattern pattern = Pattern.compile("\\b[0-9a-f]{5,40}\\b"); // sha
    Matcher shaMatcher = pattern.matcher(runBuckResult.getStdout());
    assertThat(shaMatcher.find(), Matchers.equalTo(true));
    String shaValue = shaMatcher.group();
    assertThat(shaValue.length(), Matchers.equalTo(40));
    assertThat(runBuckResult.getStdout(), Matchers.containsString("//:bar " + shaValue));
  }

  @Test
  public void showRuleKeyAndOutput() throws IOException {
    ProjectWorkspace workspace = TestDataHelper.createProjectWorkspaceForScenario(
        this, "just_build", tmp);
    workspace.setUp();
    ProjectWorkspace.ProcessResult runBuckResult = workspace.runBuckBuild(
        "--show-output",
        "--show-rulekey",
        "//:bar");
    runBuckResult.assertSuccess();

    Pattern pattern = Pattern.compile("\\b[0-9a-f]{5,40}\\b"); // sha
    Matcher shaMatcher = pattern.matcher(runBuckResult.getStdout());
    assertThat(shaMatcher.find(), Matchers.equalTo(true));
    String shaValue = shaMatcher.group();
    assertThat(shaValue.length(), Matchers.equalTo(40));
    assertThat(
        runBuckResult.getStdout(),
        Matchers.containsString("//:bar " + shaValue + " buck-out"));
  }
}
