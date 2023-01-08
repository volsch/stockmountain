/*
 * Copyright 2023 Volker Schmidt
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 *    endorse or promote products derived from this software without specific prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.volsch.stockmountain.extraction.api;

import eu.volsch.stockmountain.extraction.csv.CsvExtractionException;
import java.io.InputStream;
import java.io.Reader;
import java.util.stream.Stream;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Extracts records from an {@linkplain InputStream input stream} or {@linkplain Reader reader}.
 */
@ThreadSafe
public interface Extractor {

  /**
   * Returns if it is supported to use a {@linkplain Reader reader} instead of an
   * {@linkplain InputStream input stream}. If it is not supported to use a reader, then
   * {@link  #extract(Reader)} may throw a {@link UnsupportedOperationException}.
   *
   * @return if using a {@linkplain Reader reader} is supported.
   * @see #extract(Reader)
   */
  boolean readerSupported();

  /**
   * Extracts the records from the specified reader to the returned stream. Stream operations may
   * throw a {@link ExtractionDataAccessException} if reading the characters from the underlying
   * input stream fails or a {@link CsvExtractionException} if the input stream returns any invalid
   * data.
   *
   * @param inputStream the input stream from which the records should be read.
   * @return the stream with the resulting records.
   */
  @NonNull Stream<Record> extract(@NonNull InputStream inputStream);

  /**
   * Extracts the records from the specified reader to the returned stream. Stream operations may
   * throw a {@link ExtractionDataAccessException} if reading the characters from the underlying
   * reader fails or a {@link CsvExtractionException} if the reader returns any invalid data. This
   * method throws a {@link UnsupportedOperationException} if using a reader is not supported.
   *
   * @param reader the reader from which the records should be read.
   * @return the stream with the resulting records.
   * @see #readerSupported()
   */
  @NonNull Stream<Record> extract(@NonNull Reader reader);
}
