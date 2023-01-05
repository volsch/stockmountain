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

package eu.volsch.stockmountain.extraction.conversion;

import net.jcip.annotations.ThreadSafe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Converts a value to a target type.
 *
 * @param <S> the source value type.
 * @param <T> the target value type.
 */
@ThreadSafe
public interface Converter<S, T> {

  /**
   * Returns the source type of the conversion.
   *
   * @return the source type of the conversion.
   */
  @NonNull Class<S> getSourceType();

  /**
   * Returns the target type of the conversion.
   *
   * @return the target type of the conversion.
   */
  @NonNull Class<T> getTargetType();

  /**
   * Converts the specified source value to the target type. The specified source value will be cast
   * to the {@linkplain #getSourceType() source type} and a {@linkplain ClassCastException} will be
   * thrown if this fails.
   *
   * @param source the source value that should be converted.
   * @return the converted source value.
   * @throws ConversionException thrown if the conversion cannot be performed.
   * @throws ClassCastException  thrown if the specified source value cannot be cast to the
   *                             {@linkplain #getSourceType() source type}
   */
  default @Nullable T castAndConvert(@Nullable Object source)
      throws ConversionException, ClassCastException {
    return convert(getSourceType().cast(source));
  }

  /**
   * Converts the specified source value to the target type.
   *
   * @param source the source value that should be converted.
   * @return the converted source value.
   * @throws ConversionException thrown if the conversion cannot be performed.
   */
  @Nullable T convert(@Nullable S source)
      throws ConversionException;
}
