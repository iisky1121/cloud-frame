package com.jfinal.ext.plugin.sql;

@SuppressWarnings("unchecked")
abstract class CndBaseSelect<M extends CndBaseSelect<M>> extends Cnd {
	private CndWhere where = new CndWhere();
	private String selectSql;
	private String fromSql;
	
	public M select(String select){
		this.selectSql = select;
		sql.append(select);
		return (M)this;
	}
	
	public M from(String from){
		this.fromSql = from;
		sql.append(" ").append(from);
		return (M)this;
	}

	public M where(){
		where.setHasWhere(true);
		return (M)this;
	}


	public M and(Enum<?> key, Object val){
		return and(key.name(), val);
	}
	public M and(String key, Object val){
		where.and(key, val, val.getClass());
		return (M)this;
	}

	public M and(Enum<?> key, Object val, Class<?> classType){
		return and(key.name(), val, classType);
	}
	public M and(String key, Object val, Class<?> classType){
		where.and(key, val, classType);
		return (M)this;
	}

	public M and(Enum<?> key, Cnd.Type type, Object val){
		return and(key.name(), type, val);
	}
	public M and(String key, Cnd.Type type, Object val){
		where.and(key, type, val);
		return (M)this;
	}


	public M or(Enum<?> key, Object val){
		return or(key.name(), val);
	}
	public M or(String key, Object val){
		where.or(key, val, val.getClass());
		return (M)this;
	}

	public M or(Enum<?> key, Object val, Class<?> classType){
		return or(key.name(), val, classType);
	}
	public M or(String key, Object val, Class<?> classType){
		where.or(key, val, classType);
		return (M)this;
	}

	public M or(Enum<?> key, Cnd.Type type, Object val){
		return or(key.name(), type, val);
	}
	public M or(String key, Cnd.Type type, Object val){
		where.or(key, type, val);
		return (M)this;
	}

	public M andGroup(CndGroup group){
		where.andGroup(group);
		return (M)this;
	}

	public M orGroup(CndGroup group){
		where.orGroup(group);
		return (M)this;
	}

	public CndWhere getWhere() {
		return where;
	}
	
	public String getSelectSql() {
		return selectSql;
	}

	public String getFromSql() {
		return fromSql;
	}

	public abstract M build();
}
