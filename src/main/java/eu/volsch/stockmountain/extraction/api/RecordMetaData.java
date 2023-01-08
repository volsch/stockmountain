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

import java.util.stream.Stream;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The metadata of a record.
 */
@Immutable
@ThreadSafe
public interface RecordMetaData {

  /**
   * Returns a stream with fields that are included in this record. The fields are ordered by their
   * ordinal number.
   *
   * @return stream of fields that are included in this record.
   */
  @SuppressWarnings("java:S1452")
  @NonNull Stream<@NonNull Field<?>> fieldStream();

  /**
   * Returns if the record contains the specified field.
   *
   * @param field the field that should be checked.
   * @return if the field in included in this record.
   */
  boolean containsField(@NonNull Field<?> field);

  /**
   * Returns the maximum ordinal value of any field that is included in the recorc.
   *
   * @return the maximum field ordinal.
   */
  @NonNegative int getMaxFieldOrdinal();

  /**
   * Returns the number of fields of a record.
   *
   * @return the number of fields of a record.
   */
  @NonNegative int getFieldCount();

  /**
   * Returns the field at the specified index.
   *
   * @param index the zero-based index of the field.
   * @return the field at the specified index.
   * @throws IllegalArgumentException thrown if the specified index is negative or equal or greater
   *                                  than the {@linkplain #getFieldCount() field count}.
   */
  @SuppressWarnings("java:S1452")
  @NonNull Field<?> getField(@NonNegative int index) throws IllegalArgumentException;
}
