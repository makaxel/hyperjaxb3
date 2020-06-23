package org.jvnet.hyperjaxb3.ejb.strategy.model.base;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jvnet.hyperjaxb3.ejb.strategy.model.ProcessClassInfo;
import org.jvnet.hyperjaxb3.ejb.strategy.model.ProcessModel;

import java.util.Collection;
import java.util.HashSet;

public class DefaultProcessClassInfo implements ProcessClassInfo {

	protected Log logger = LogFactory.getLog(getClass());

	public Collection<CClassInfo> process(ProcessModel context,
			CClassInfo classInfo) {


		logger.debug(" D in org.jvnet.hyperjaxb3.ejb.strategy.model.base.DefaultProcessClassInfo.process");

		final Collection<CPropertyInfo> newProperties = context
				.getProcessPropertyInfos().process(context, classInfo);

		final Collection<CClassInfo> classes = new HashSet<CClassInfo>(1);

		classes.add(classInfo);

		for (CPropertyInfo newProperty : newProperties) {
			if (newProperty.parent() == null) {
				throw new IllegalStateException("Property ["
						+ newProperty.getName(true)
						+ "] does not have a parent.");
			}
			classes.add((CClassInfo) newProperty.parent());
		}
		
		classes.addAll(context.getCreateIdClass().process(context, classInfo));

		return classes;
	}
}
