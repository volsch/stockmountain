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
import java.time.LocalDate;
import java.time.LocalTime;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Transaction of buying or selling securities.
 */
public interface Transaction {

  /**
   * The unique ID of the transaction within an account at a broker. The unique ID must be an
   * incrementing value. A transaction that has been executed before another transaction within the
   * same account at the same broker must have a smaller ID than the following transaction.
   *
   * @return the unique ID of this transaction.
   */
  @NonNegative long getId();

  /**
   * The incrementing version of this transaction. Each time the transaction is persisted, this
   * version increments. The version can be used for optimistic locking.
   *
   * @return the incrementing version of this transaction.
   */
  @NonNegative int getVersion();

  /**
   * Returns the type of the transaction.
   *
   * @return the type of the transaction.
   */
  @NonNull TransactionType getType();

  /**
   * Returns if this transaction represents a fictional purchase or sale. For example, a fictional
   * transaction might be used in case of a split of stocks. If 1 stock is split into 20 stocks and
   * an account has 15 of these stocks, then there might be a fictional sale of 15 stocks followed
   * by fictional purchase of 300 stocks.
   *
   * @return if this transaction is fictional.
   */
  boolean isFictional();

  /**
   * Returns the date when this transaction has been executed.
   *
   * @return the date when this transaction has been executed.
   */
  @NonNull LocalDate getDate();

  /**
   * Returns the time of the day when this transaction has been executed. The time of the execution
   * may not be available at all and also its precision may vary depending on the broker.
   *
   * @return the time when this transaction has been executed.
   * @see #getDate()
   */
  @Nullable LocalTime getTime();

  /**
   * Returns the ISIN of the purchased or sold security.
   *
   * @return the ISIN of the value.
   */
  @NonNull String getIsin();

  /**
   * Returns the stock ticker symbol of the purchased or sold security.
   *
   * @return the stock ticker symbol or <code>null</code> if this is not known.
   */
  @Nullable String getTickerSymbol();

  /**
   * Returns the name of the associated security.
   *
   * @return the name of the associated security.
   */
  @NonNull String getName();

  /**
   * Returns the symbolic name of the securities exchange where this transaction has been handled.
   * No specific standardisation is used. Examples for symbolic names are NASDAQ, NYSE (The New York
   * Stock Exchange), XETRA, FRA (Frankfurt Stock Exchange).
   *
   * @return the symbolic name of the securities exchange.
   */
  @Nullable String getSecuritiesExchange();

  /**
   * Returns the quantity of purchased or sold securities. In case of a purchase the returned value
   * must be positive. In case of a sale the returned value must be negative. Values with decimal
   * places are supported.
   *
   * @return the quantity of securities.
   */
  @NonNull BigDecimal getQuantity();

  /**
   * Returns the local price of a single unit (quantity one) of the security. The price is local to
   * the securities exchange where the securities are traded. The price cannot be a negative value.
   *
   * @return the local price of a single unit of the security.
   */
  @Nullable Price getLocalPrice();

  /**
   * Returns the local value of the securities that are traded by this transaction. The value is
   * local to the securities exchange where the securities are traded. This local value is the
   * result of multiplying the {@linkplain #getLocalPrice() local price} by the
   * {@linkplain #getQuantity() quantity}. In case of a purchase the returned value must be
   * negative. In case of a sale the returned value must be positive.
   *
   * @return the local value of the securities.
   * @see #getValue()
   */
  @Nullable Price getLocalValue();

  /**
   * Returns the exchange rate between the {@linkplain #getValue() value} and the
   * {@linkplain #getLocalValue() local value}. For example, if the local value is 1 USD and the
   * exchange rate to the account currency EUR is 1.0346, then the value is 1.0346 EUR.
   *
   * @return the exchange rate between the value and the local value.
   * @see #getLocalValue()
   * @see #getValue()
   */
  @Nullable
  @NonNegative
  BigDecimal getExchangeRate();

  /**
   * Returns the value of the securities that are traded by this transaction. The value uses the
   * currency of the account at the broker. This value is the result of dividing the
   * {@linkplain #getLocalValue() local value} by the {@linkplain #getExchangeRate() exchange rate}.
   * In case of a purchase the returned value must be negative. In case of a sale the returned value
   * must be positive.
   *
   * @return the value of the securities in the currency of the account at the broker.
   * @see #getLocalValue()
   * @see #getExchangeRate()
   */

  @Nullable Price getValue();

  /**
   * Returns the commission to be paid for this transaction. Since the commission needs to be paid
   * by the purchaser or seller, the value must be negative normally.
   *
   * @return the commission to be paid for this transaction.
   */
  @Nullable Price getCommission();

  /**
   * Returns the total amount to be paid or returned for this transaction. If this amount is
   * positive, the amount is returned. If this amount is negative, the amount must be paid. This
   * amount is normally calculated by adding the {@linkplain #getValue() value} and the
   * {@linkplain #getCommission() commission}.
   *
   * @return the total amount to be paid or returned.
   */
  @NonNull Price getTotal();

  /**
   * Returns the broker generated ID of the order to which this transaction belongs to. One order
   * may contain several transactions.
   *
   * @return the broker generated ID of the order.
   */
  @Nullable String getOrderId();

  /**
   * Returns the broker generated ID of the transaction.
   *
   * @return the broker generated ID of the transaction.
   */
  @Nullable String getTransactionId();
}
