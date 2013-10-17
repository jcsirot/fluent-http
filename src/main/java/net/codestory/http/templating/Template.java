/**
 * Copyright (C) 2013 all@code-story.net
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
package net.codestory.http.templating;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import net.codestory.http.compilers.Compiler;
import net.codestory.http.io.*;
import net.codestory.http.types.*;

public class Template {
  private final Path path;

  public Template(String url) {
    this(Paths.get(url));
  }

  public Template(Path path) {
    this.path = findExistingPath(path);
  }

  private static Path findExistingPath(Path path) {
    for (String extension : ContentTypes.TEMPLATE_EXTENSIONS) {
      Path templatePath = Paths.get(path.toString() + extension);
      if (Resources.exists(templatePath)) {
        return templatePath;
      }
    }
    throw new IllegalArgumentException("Template not found " + path);
  }

  public String render() {
    return render(Model.of());
  }

  public String render(String key, Object value) {
    return render(Model.of(key, value));
  }

  public String render(String k1, Object v1, String k2, Object v2) {
    return render(Model.of(k1, v1, k2, v2));
  }

  public String render(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
    return render(Model.of(k1, v1, k2, v2, k3, v3));
  }

  public String render(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
    return render(Model.of(k1, v1, k2, v2, k3, v3, k4, v4));
  }

  public String render(Model model) {
    return render(model.getKeyValues());
  }

  public String render(Map<String, Object> keyValues) {
    return render(Site.get(), keyValues);
  }

  String render(Site site, Model model) {
    return render(site, model.getKeyValues());
  }

  String render(Site site, Map<String, Object> keyValues) {
    try {
      YamlFrontMatter parsedTemplate = YamlFrontMatter.parse(path);
      Map<String, Object> allKeyValues = merge(parsedTemplate.getVariables(), keyValues);

      String content = Compiler.compile(path, parsedTemplate.getContent());
      String body = new HandlebarsCompiler().compile(content, site, allKeyValues);

      String layout = (String) parsedTemplate.getVariables().get("layout");
      if (layout != null) {
        for (String extension : ContentTypes.TEMPLATE_EXTENSIONS) {
          Path layoutPath = Paths.get("_layouts", layout + extension);
          if (Resources.exists(layoutPath)) {
            return new Template(layoutPath).render(site, allKeyValues).replace("[[body]]", body);
          }
        }
        throw new IllegalStateException("Unable to find layout: " + layout);
      }

      return body;
    } catch (IOException e) {
      throw new IllegalStateException("Unable to render template", e);
    }
  }

  @SafeVarargs
  private static Map<String, Object> merge(Map<String, Object>... keyValues) {
    Map<String, Object> merged = new HashMap<>();
    for (Map<String, Object> keyValue : keyValues) {
      merged.putAll(keyValue);
    }
    merged.put("body", "[[body]]");
    return merged;
  }
}
