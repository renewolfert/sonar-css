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
package org.sonar.css.checks;

import com.sonar.sslr.api.AstNode;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.css.checks.utils.CssProperties;
import org.sonar.css.checks.utils.Vendors;
import org.sonar.css.parser.CssGrammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.annotations.SqaleSubCharacteristic;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

/**
 * https://github.com/stubbornella/csslint/wiki/Require-standard-property-with-vendor-prefix
 * @author tkende
 *
 */
@Rule(
  key = "vendor-prefix",
  name = "Standard properties should be specified along with vendor-prefixed properties",
  priority = Priority.MAJOR,
  tags = {Tags.BROWSER_COMPATIBILITY})
@SqaleSubCharacteristic(RulesDefinition.SubCharacteristics.ARCHITECTURE_RELIABILITY)
@SqaleConstantRemediation("10min")
@ActivatedByDefault
public class VendorPrefixWithStandard extends SquidCheck<LexerlessGrammar> {

  @Override
  public void init() {
    subscribeTo(CssGrammar.DECLARATION);
  }

  @Override
  public void leaveNode(AstNode astNode) {
    String property = astNode.getFirstChild(CssGrammar.PROPERTY).getTokenValue();
    if (Vendors.isVendorPrefixed(property) && CssProperties.getProperty(CssProperties.getPropertyNameWithoutVendorPrefix(
        property)) != null) {
      if (!isNextExists(astNode, CssProperties.getPropertyNameWithoutVendorPrefix(property))) {
        getContext().createLineViolation(this, "Define the standard property after this vendor-prefixed property", astNode);
      }
    }
  }

  private boolean isNextExists(AstNode actual, String propertyName) {
    AstNode next = actual.getNextSibling();
    while (next != null) {
      AstNode property = next.getFirstChild(CssGrammar.PROPERTY);
      if (property != null) {
        String nextProperty = property.getTokenValue();
        if (propertyName.equalsIgnoreCase(nextProperty)) {
          return true;
        }
      }
      next = next.getNextSibling();
    }
    return false;
  }
}
