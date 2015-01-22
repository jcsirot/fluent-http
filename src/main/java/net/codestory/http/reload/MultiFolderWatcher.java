/**
 * Copyright (C) 2013-2014 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.http.reload;

import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.util.List;

public class MultiFolderWatcher {
  private final List<FolderWatcher> classesWatchers;

  public MultiFolderWatcher(List<Path> paths, FolderChangeListener listener) {
    this.classesWatchers = paths.stream().map(path -> new FolderWatcher(path, listener)).collect(toList());
  }

  public void ensureStarted() {
    classesWatchers.forEach(watcher -> watcher.ensureStarted());
  }

  public void stop() {
    classesWatchers.forEach(watcher -> watcher.stop());
  }
}