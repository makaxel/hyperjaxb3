package org.jvnet.hyperjaxb3.ejb.strategy.mapping;

import com.sun.java.xml.ns.persistence.orm.Attributes;
import com.sun.java.xml.ns.persistence.orm.Entity;
import com.sun.java.xml.ns.persistence.orm.Inheritance;
import com.sun.java.xml.ns.persistence.orm.Table;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jvnet.hyperjaxb3.ejb.schemas.customizations.Customizations;
import org.jvnet.jaxb2_commons.util.CustomizationUtils;

import javax.persistence.InheritanceType;

public class EntityMapping implements ClassOutlineMapping<Entity> {

	 private static Log logger = LogFactory.getLog(EntityMapping.class);

	public Entity process(Mapping context, ClassOutline classOutline,
			Options options) {

		logger.debug("in EntityMapping.process");

		final Entity entity = context.getCustomizing().getEntity(classOutline);
		logger.debug(" == got entity = " + entity.getClazz());

		createEntity(context, classOutline, entity);
		return entity;
	}

	public void createEntity(Mapping context, ClassOutline classOutline,
			final Entity entity) {

		logger.debug("in EntityMapping.createEntity");

		logger.debug("0. classOutline.target.getTypeName="+classOutline.target.getTypeName());
		logger.debug("0. classOutline.target.getType="+classOutline.target.getType());
		logger.debug("0. classOutline.target.getName="+classOutline.target.getName());
		logger.debug("0. classOutline.target.getBaseClass="+classOutline.target.getBaseClass());
		logger.debug("0. classOutline.target.getSqueezedName="+classOutline.target.getSqueezedName());
		logger.debug("0. classOutline.target.getUserSpecifiedImplClass="+classOutline.target.getUserSpecifiedImplClass());
		logger.debug("0. classOutline.target.getClazz="+classOutline.target.getClazz());
		logger.debug("0. classOutline.target.getElementName="+classOutline.target.getElementName());
//[DEBUG] 0. classOutline.target.getTypeName={urn:iso:std:iso:20022:tech:xsd:camt.016.001.04}Document
//[DEBUG] 0. classOutline.target.getType=iso.std.iso._20022.tech.xsd.camt_016_001.Document
//[DEBUG] 0. classOutline.target.getName=iso.std.iso._20022.tech.xsd.camt_016_001.Document
//[DEBUG] 0. classOutline.target.getBaseClass=null
//[DEBUG] 0. classOutline.target.getSqueezedName=Document
//[DEBUG] 0. classOutline.target.getUserSpecifiedImplClass=null
//[DEBUG] 0. classOutline.target.getClazz=iso.std.iso._20022.tech.xsd.camt_016_001.Document
//[DEBUG] 0. classOutline.target.getElementName=null

		// TODO: 23.06.2020 если это Document, то надо неймспейс из getTypeName выкусить и вставить в поле этой таблицы
			// 		клас-нейм и энтити-нейм сформоровать путем склейки Document и последней части неймспейса
			// 		например Document_camt01600104


		logger.debug("1. createEntity$Name");
		createEntity$Name(context, classOutline, entity);
		if (entity.getName().equalsIgnoreCase("Document") && classOutline.target.getTypeName()!=null ){
			String namespace = classOutline.target.getTypeName().getNamespaceURI();
			String lastPartOfNamespace = StringUtils.substringAfterLast(namespace, ":");  //namespace.substring(namespace.lastIndexOf(":")+1);
			String lastPartOfNamespaceWithoutSpecSymbols = lastPartOfNamespace.replaceAll("[:.]", "");
			String entityName = classOutline.target.getSqueezedName() + "_" + lastPartOfNamespaceWithoutSpecSymbols;
			String tableName = classOutline.target.getSqueezedName() + "_" + lastPartOfNamespaceWithoutSpecSymbols;
			String clazzName = classOutline.target.getSqueezedName() + "_" + lastPartOfNamespaceWithoutSpecSymbols;
			logger.debug("0. entityName="+entityName);
			logger.debug("0. tableName="+tableName);
			logger.debug("0. clazzName="+clazzName);
			entity.setName(entityName);
			entity.setClazz(clazzName);
			entity.setTable(new Table());
			entity.getTable().setName(tableName);
		}
		logger.debug("1. got createEntity$Name = " + entity.getName());

//		if (entity.getName().equalsIgnoreCase("Document")){
//			logger.debug("   skip Document");
//			return;
//		}



		logger.debug("2. createEntity$Class");
		createEntity$Class(context, classOutline, entity);
		logger.debug("2. got createEntity$Class = " + entity.getClazz());

		logger.debug("3. createEntity$Inheritance");
		createEntity$Inheritance(context, classOutline, entity);
		logger.debug("3. got createEntity$Inheritance = " + entity.getInheritance());

		logger.debug("4. createEntity$Table");
		createEntity$Table(context, classOutline, entity);
		logger.debug("4. got createEntity$Table = " + entity.getTable().getName());

		logger.debug("5. createEntity$Attributes");
		createEntity$Attributes(context, classOutline, entity);
		logger.debug("5. got createEntity$Attributes = " + entity.getAttributes() );
	}

	public void createEntity$Name(Mapping context, ClassOutline classOutline,
			final Entity entity) {
		if (entity.getName() == null || "##default".equals(entity.getName())) {
			entity.setName(context.getNaming().getEntityName(context,
					classOutline.parent(), classOutline.target));
		}
	}

	public void createEntity$Class(Mapping context, ClassOutline classOutline,
			final Entity entity) {
		if (entity.getClazz() == null || "##default".equals(entity.getClazz())) {
			entity.setClazz(context.getNaming().getEntityClass(context,
					classOutline.parent(), classOutline.target));
		}
	}

	public void createEntity$Inheritance(Mapping context,
			ClassOutline classOutline, final Entity entity) {
		final InheritanceType inheritanceStrategy = getInheritanceStrategy(
				context, classOutline, entity);

		if (isRootClass(context, classOutline)) {
			if (entity.getInheritance() == null
					|| entity.getInheritance().getStrategy() == null) {
				entity.setInheritance(new Inheritance());
				entity.getInheritance().setStrategy(inheritanceStrategy.name());
			}
		} else {
			if (entity.getInheritance() != null
					&& entity.getInheritance().getStrategy() != null) {
				entity.setInheritance(null);
			}
		}
	}

	private void createEntity$Table(Mapping context, ClassOutline classOutline,
			Entity entity) {
		final InheritanceType inheritanceStrategy = getInheritanceStrategy(
				context, classOutline, entity);
		switch (inheritanceStrategy) {
		case JOINED:
			if (entity.getTable() == null) {
				entity.setTable(new Table());
			}
			createTable(context, classOutline, entity.getTable());
			break;
		case SINGLE_TABLE:
			if (isRootClass(context, classOutline)) {
				if (entity.getTable() == null) {
					entity.setTable(new Table());
				}
				createTable(context, classOutline, entity.getTable());
			} else {
				if (entity.getTable() != null) {
					entity.setTable(null);
				}
			}
			break;
		case TABLE_PER_CLASS:
			if (entity.getTable() == null) {
				entity.setTable(new Table());
			}
			createTable(context, classOutline, entity.getTable());
			break;
		default:
			throw new IllegalArgumentException("Unknown inheritance strategy.");
		}
	}

	public void createTable(Mapping context, ClassOutline classOutline,
			final Table table) {
		if (table.getName() == null || "##default".equals(table.getName())) {
			table.setName(context.getNaming().getEntityTable$Name(context,
					classOutline));
		}
	}

	public void createEntity$Attributes(Mapping context,
			ClassOutline classOutline, final Entity entity) {

		logger.debug("5.1. in org.jvnet.hyperjaxb3.ejb.strategy.mapping.EntityMapping.createEntity$Attributes");


		final Attributes attributes = context.getAttributesMapping().process(
				context, classOutline, null);
		entity.setAttributes(attributes);
	}

	public javax.persistence.InheritanceType getInheritanceStrategy(
			Mapping context, ClassOutline classOutline, Entity entity) {
		if (isRootClass(context, classOutline)) {
			if (entity.getInheritance() != null
					&& entity.getInheritance().getStrategy() != null) {
				return InheritanceType.valueOf(entity.getInheritance()
						.getStrategy());
			} else {
				return javax.persistence.InheritanceType.JOINED;
			}
		} else {
			final ClassOutline superClassOutline = getSuperClass(context,
					classOutline);
			final Entity superClassEntity = context.getCustomizing().getEntity(
					superClassOutline);

			return getInheritanceStrategy(context, superClassOutline,
					superClassEntity);
		}
	}

	public ClassOutline getSuperClass(Mapping context, ClassOutline classOutline) {
		return classOutline.getSuperClass();
	}

	/*
	 * public ClassOutline getSuperClassOutline(Mapping context, ClassOutline
	 * classOutline) { return classOutline.getSuperClass(); }
	 * 
	 * public boolean isEntityClassHierarchyRoot(Mapping context, ClassOutline
	 * classOutline) { final ClassOutline superClassOutline =
	 * getSuperClassOutline(context, classOutline);
	 * 
	 * if (superClassOutline == null) { return true; } else if
	 * (CustomizationUtils.containsCustomization(classOutline,
	 * Customizations.MAPPED_SUPERCLASS_ELEMENT_NAME)) { return true; } else if
	 * (context.getIgnoring().isClassOutlineIgnored( superClassOutline)) {
	 * return true; } else { return false; } }
	 */

	public boolean isRootClass(Mapping context, ClassOutline classOutline) {
		if (classOutline.getSuperClass() != null) {
			return !CustomizationUtils.containsCustomization(classOutline,
					Customizations.MAPPED_SUPERCLASS_ELEMENT_NAME)
					&& !isSelfOrAncestorRootClass(context,
							classOutline.getSuperClass());
		} else {
			return !CustomizationUtils.containsCustomization(classOutline,
					Customizations.MAPPED_SUPERCLASS_ELEMENT_NAME);
		}
	}

	public boolean isSelfOrAncestorRootClass(Mapping context,
			ClassOutline classOutline) {
		if (context.getIgnoring().isClassOutlineIgnored(context, classOutline)) {
			return false;
		} else if (isRootClass(context, classOutline)) {
			return true;
		} else if (classOutline.getSuperClass() != null) {
			return isSelfOrAncestorRootClass(context,
					classOutline.getSuperClass());
		} else {
			return !CustomizationUtils.containsCustomization(classOutline,
					Customizations.MAPPED_SUPERCLASS_ELEMENT_NAME);
		}

	}

}
