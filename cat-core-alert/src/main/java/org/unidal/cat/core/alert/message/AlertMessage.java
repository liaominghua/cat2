package org.unidal.cat.core.alert.message;

import org.unidal.cat.core.alert.data.entity.AlertDataSegment;
import org.unidal.cat.core.alert.rules.entity.AlertRuleDef;

public interface AlertMessage {
   public AlertRuleDef getRule();

   public AlertDataSegment getSegment();
}
