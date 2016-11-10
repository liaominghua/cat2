package org.unidal.cat.core.message.page.home;

import org.unidal.cat.core.message.page.MessagePage;
import org.unidal.web.mvc.view.BaseJspViewer;

public class JspViewer extends BaseJspViewer<MessagePage, Action, Context, Model> {
   @Override
   protected String getJspFilePath(Context ctx, Model model) {
      Action action = model.getAction();

      switch (action) {
      case DEFAULT:
         return JspFile.VIEW.getPath();
      default:
         break;
      }

      throw new RuntimeException("Unknown action: " + action);
   }
}
