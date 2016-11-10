package org.unidal.cat.plugin.transaction;

import static org.unidal.cat.core.config.spi.ConfigStoreManager.GROUP_REPORT;
import static org.unidal.cat.plugin.transaction.TransactionConstants.NAME;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.cat.core.config.spi.ConfigChangeListener;
import org.unidal.cat.core.config.spi.ConfigException;
import org.unidal.cat.core.config.spi.ConfigStore;
import org.unidal.cat.core.config.spi.ConfigStoreManager;
import org.unidal.cat.plugin.transaction.config.entity.IgnoreModel;
import org.unidal.cat.plugin.transaction.config.entity.TransactionConfigModel;
import org.unidal.cat.plugin.transaction.config.transform.DefaultSaxParser;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named
public class TransactionConfigService implements Initializable, ConfigChangeListener {
   @Inject
   private ConfigStoreManager m_manager;

   private Set<String> m_matchedDomains;

   private List<String> m_startingDomains;

   @Override
   public void initialize() throws InitializationException {
      m_manager.register(GROUP_REPORT, NAME, this);

      ConfigStore store = m_manager.getConfigStore(GROUP_REPORT, NAME);
      String config = store.getConfig();

      try {
         if (config != null) {
            TransactionConfigModel root = DefaultSaxParser.parse(config);

            initialize(root);
         }
      } catch (Exception e) {
         throw new InitializationException("Invalid transaction config:\r\n" + config, e);
      }
   }

   private void initialize(TransactionConfigModel root) {
      Set<String> matchedDomains = new HashSet<String>();
      List<String> startingDomains = new ArrayList<String>();

      for (IgnoreModel ignore : root.getIgnores()) {
         String domain = ignore.getDomain();

         if (domain.endsWith("*")) {
            String prefix = domain.substring(0, domain.length() - 1);

            if (!startingDomains.contains(prefix)) {
               startingDomains.add(prefix);
            }
         } else {
            matchedDomains.add(domain);
         }
      }

      m_matchedDomains = matchedDomains;
      m_startingDomains = startingDomains;
   }

   public boolean isEligible(String domain) {
      if (m_matchedDomains.contains(domain)) {
         return false;
      }

      for (String startingDomain : m_startingDomains) {
         if (domain.startsWith(startingDomain)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void onChanged(String config) throws ConfigException {
      try {
         if (config != null) {
            TransactionConfigModel root = DefaultSaxParser.parse(config);

            initialize(root);
         }
      } catch (Exception e) {
         throw new ConfigException("Invalid transaction config:\r\n" + config, e);
      }
   }
}
