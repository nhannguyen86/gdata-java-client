/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serializes HTTP request content from an input stream into an output stream.
 * <p>
 * The {@link #type} and {@link #inputStream} fields are required. The input
 * stream is guaranteed to be closed at the end of
 * {@link #writeTo(OutputStream)}.
 * <p>
 * For a file input, use {@link #setFileInput(File)}, and for a byte array or
 * string input use {@link #setByteArrayInput(byte[])}.
 * <p>
 * Sample use with a URL:
 * 
 * <pre>
 * <code>
 * private static void setRequestJpegContent(HttpRequest request, URL jpegUrl) {
 *   InputStreamContent content = new InputStreamContent();
 *   content.inputStream = jpegUrl.openStream();
 *   content.type = "image/jpeg";
 *   request.content = content;
 * }
 * </code>
 * </pre>
 * 
 * @since 2.2
 * @author Yaniv Inbar
 */
public final class InputStreamContent implements HttpContent {

  private final static int BUFFER_SIZE = 2048;

  /** Required content type. */
  public String type;

  /** Content length or less than zero if not known. Defaults to {@code -1}. */
  public long length = -1;

  /** Required input stream to read from. */
  public InputStream inputStream;

  /**
   * Content encoding (for example {@code "gzip"}) or {@code null} for none.
   */
  public String encoding;

  /**
   * Sets the {@link #inputStream} from a file input stream based on the given
   * file, and the {@link #length} based on the file's length.
   * <p>
   * Sample use:
   * 
   * <pre>
   * <code>
   * private static void setRequestJpegContent(HttpRequest request, File jpegFile) {
   *   InputStreamContent content = new InputStreamContent();
   *   content.setFileInput(jpegFile);
   *   content.type = "image/jpeg";
   *   request.content = content;
   * }
   * </code>
   * </pre>
   */
  public void setFileInput(File file) throws FileNotFoundException {
    this.inputStream = new FileInputStream(file);
    this.length = file.length();
  }

  /**
   * Sets the {@link #inputStream} and {@link #length} from the given byte
   * array.
   * <p>
   * For string input, call the appropriate {@link String#getBytes} method.
   * <p>
   * Sample use:
   * 
   * <pre>
   * <code>
   * private static void setRequestJsonContent(HttpRequest request, String json) {
   *   InputStreamContent content = new InputStreamContent();
   *   content.setByteArrayInput(json.getBytes());
   *   content.type = "application/json";
   *   request.content = content;
   * }
   * </code>
   * </pre>
   * 
   * @since 2.3
   */
  public void setByteArrayInput(byte[] content) {
    this.inputStream = new ByteArrayInputStream(content);
    this.length = content.length;
  }

  public void writeTo(OutputStream out) throws IOException {
    InputStream inputStream = this.inputStream;
    long contentLength = this.length;
    if (contentLength < 0) {
      copy(inputStream, out);
    } else {
      byte[] buffer = new byte[BUFFER_SIZE];
      try {
        // consume no more than length
        long remaining = contentLength;
        while (remaining > 0) {
          int read =
              inputStream.read(buffer, 0, (int) Math
                  .min(BUFFER_SIZE, remaining));
          if (read == -1) {
            break;
          }
          out.write(buffer, 0, read);
          remaining -= read;
        }
      } finally {
        inputStream.close();
      }
    }
  }

  public String getEncoding() {
    return this.encoding;
  }

  public long getLength() {
    return this.length;
  }

  public String getType() {
    return this.type;
  }

  /**
   * Writes the content provided by the given source input stream into the given
   * destination output stream.
   * <p>
   * The input stream is guaranteed to be closed at the end of the method.
   * 
   * @param inputStream source input stream
   * @param outputStream destination output stream
   * @throws IOException I/O exception
   * @since 2.3
   */
  public static void copy(InputStream inputStream, OutputStream outputStream)
      throws IOException {
    try {
      byte[] tmp = new byte[BUFFER_SIZE];
      int bytesRead;
      while ((bytesRead = inputStream.read(tmp)) != -1) {
        outputStream.write(tmp, 0, bytesRead);
      }
    } finally {
      inputStream.close();
    }
  }
}