/*
 * SonarQube CSS Plugin
 * Copyright (C) 2013 Tamas Kende and David RACODON
 * kende.tamas@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.css.checks.validators;

import com.google.common.collect.ImmutableList;
import org.sonar.css.checks.utils.CssValueElement;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

public class ValueElementMultiValidator implements ValueElementValidator {

  private List<? extends ValueElementValidator> validators = new ArrayList<>();

  public ValueElementMultiValidator() {
  }

  public ValueElementMultiValidator(@Nonnull ImmutableList<? extends ValueElementValidator> validators) {
    this.validators = validators;
  }

  @Override
  public boolean isValid(@Nonnull CssValueElement cssValueElement) {
    for (ValueElementValidator validator : validators) {
      if (validator.isValid(cssValueElement)) {
        return true;
      }
    }
    return false;
  }

  @Override
  @Nonnull
  public String getValidatorFormat() {
    StringBuilder format = new StringBuilder();
    for (Validator validator : validators) {
      if (format.length() > 0) {
        format.append(" | ");
      }
      format.append(validator.getValidatorFormat());
    }
    return format.toString();
  }

}
