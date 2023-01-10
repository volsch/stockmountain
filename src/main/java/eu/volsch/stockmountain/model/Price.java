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

package eu.volsch.stockmountain.model;

import java.math.BigDecimal;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Contains the price as value and currency.
 */
public final class Price implements Comparable<Price> {

  private final @NonNull BigDecimal value;
  private final @NonNull String currency;

  public Price(@NonNull BigDecimal value, @NonNull String currency) {
    this.value = value;
    this.currency = currency;
  }

  public @NonNull BigDecimal getValue() {
    return value;
  }

  public @NonNull String getCurrency() {
    return currency;
  }

  @Override
  public int compareTo(@NonNull Price o) {
    final int result = currency.compareTo(o.currency);
    if (result != 0) {
      return result;
    }
    return value.compareTo(o.value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Price price = (Price) o;
    return value.compareTo(price.value) == 0 && currency.equals(price.currency);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value.doubleValue(), currency);
  }

  @Override
  public String toString() {
    return value + " " + currency;
  }
}
