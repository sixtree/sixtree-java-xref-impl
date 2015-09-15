package au.com.sixtree.xref;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import au.com.sixtree.xref.cache.CacheAccessor;
import au.com.sixtree.xref.model.EntityNotFoundException;
import au.com.sixtree.xref.model.Relation;
import au.com.sixtree.xref.model.Relation.Reference;
import au.com.sixtree.xref.model.RelationFactory;

public class JDBCXrefOperation implements XrefOperation {
	
	private JdbcTemplate jdbcTemplate;
	private CacheAccessor cacheAccessor = new CacheAccessor();

	public Relation findRelation(String entitySet, String tenant, String endpoint, String endpointId) throws EntityNotFoundException {
		Relation cachedRelation = cacheAccessor.getRelationByEndpoint(tenant, entitySet, endpoint, endpointId);
		if(cachedRelation != null) {
			return cachedRelation;
		} else {
			Integer entityTypeId = findEntityType(tenant, entitySet);
			Relation uncachedRelation = findRelationByEndpointAndEndpointID(entityTypeId, endpoint, endpointId);
			cacheAccessor.putRelationByEndpoint(tenant, entitySet, endpoint, endpointId, uncachedRelation);
			return uncachedRelation;
		}
	}

	public Relation createRelation(final String entitySet, final String tenant, Relation relation) throws EntityNotFoundException {
		Integer entityTypeId = findOrCreateEntityType(tenant, entitySet);
		Integer relationId = saveRelation(entityTypeId);
		for(Reference reference : relation.getReference()) {
			saveReference(relationId, reference.getEndpoint(), reference.getEndpointId());
		}
		return getRelation(relationId);
	}

	public Relation updateRelation(String entitySet, String tenant, Relation relation) throws EntityNotFoundException {
		Relation currentRelation = getRelationByCommonID(relation.getCommonID());
		for(Reference reference : relation.getReference()) {
			saveOrUpdateReference(currentRelation.getId(), relation.getCommonID(), reference.getEndpoint(), reference.getEndpointId());
		}
		return getRelation(relation.getId());
	}

	public Relation findRelationByCommonId(String commonId, String entitySet,
			String tenant) throws EntityNotFoundException {
		return getRelationByCommonID(commonId);
	}

	public Relation deleteReference(String endpoint, String commonId,
			String entitySet, String tenant) throws EntityNotFoundException {
		Relation relation = findRelationByCommonId(commonId, entitySet, tenant);
		deleteReference(relation.getId(), endpoint);
		return getRelation(relation.getId());
	}

	public Relation addOrUpdateReference(String endpointId, String endpoint,
			String commonId, String entitySet, String tenant) throws EntityNotFoundException {
		Relation relation = findRelationByCommonId(commonId, entitySet, tenant);
		saveOrUpdateReference(relation.getId(), commonId, endpoint, endpointId);;
		return getRelation(relation.getId());
	}

	private Integer findOrCreateEntityType(final String tenant, final String entitySet) {
		Integer entityTypeId = findEntityType(tenant, entitySet);
		if(entityTypeId == null) {
			KeyHolder holder = new GeneratedKeyHolder();
			jdbcTemplate.update(new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement("insert into entitytype (tenant, entitytype) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
					ps.setString(1,  tenant);
					ps.setString(2, entitySet);
					return ps;
				}
			}, holder);
			entityTypeId = holder.getKey().intValue();
		}
		return entityTypeId;
	}
	
	private Integer findEntityType(final String tenant, final String entitySet) {
		try {
			return getEntityById("select id from entitytype where tenant = ? and entitytype = ?", new Object[] {tenant, entitySet}, new RowMapper<Integer>() {
				public Integer mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					return rs.getInt("id");
				}}, "Could not find EntityType with the provided Identifier");
		} catch (EntityNotFoundException e) {
			return null;
		}
	}
	
	private Integer saveRelation(final Integer entityTypeId) {
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement("insert into relation (commonid, entitytype_id) values (?, ?)", Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, UUID.randomUUID().toString());
				ps.setInt(2, entityTypeId);
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}
	
	private Integer saveReference(final Integer relationId, final String endpoint, final String endpointId) {
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement("insert into reference (relation_id, endpoint, endpointid) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, relationId);
				ps.setString(2, endpoint);
				ps.setString(3, endpointId);
				return ps;
			}
		}, holder);
		return holder.getKey().intValue();
	}
	
	private Relation getRelation(Integer relationId) throws EntityNotFoundException {
		return getEntityById("select * from relation where id = ?", new Object[] {relationId}, new RowMapper<Relation>() {
				public Relation mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					Relation relation = RelationFactory.createRelation();
					relation.setId(rs.getInt("id"));
					relation.setCommonID(rs.getString("commonid"));
					relation.getReference().addAll(getReferences(rs.getInt("id")));
					return relation;
				}}, "Could not find Relation with the provided Identifier");
	}
	
	private Relation getRelationByCommonID(String commonID) throws EntityNotFoundException {
		return getEntityById("select * from relation where commonid = ?", new Object[] {commonID}, new RowMapper<Relation>() {
			public Relation mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				Relation relation = RelationFactory.createRelation();
				relation.setId(rs.getInt("id"));
				relation.setCommonID(rs.getString("commonid"));
				relation.getReference().addAll(getReferences(rs.getInt("id")));
				return relation;
			}}, "Could not find Relation with the provided Identifier");
	}
	
	private List<Reference> getReferences(Integer relationId) {
		return jdbcTemplate.query("select * from reference where relation_id = ?",
				new Object[] { relationId }, 
				new RowMapper<Reference>() {
					public Reference mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						Reference reference = RelationFactory.createRelationReference(
								rs.getInt("reference.id"),
								rs.getString("endpoint"),
								rs.getString("endpointid"));
						return reference;
					}
		});
	}
	
	private void saveOrUpdateReference(int relationId, String commonID, String endpoint, String endpointId) {
		try {
			Reference reference = findReferenceByEndpointAndCommonID(commonID, endpoint);
			updateReference(reference.getId(), endpoint, endpointId);
		} catch (EntityNotFoundException e) {
			saveReference(relationId, endpoint, endpointId);
		}
	}

	private void updateReference(int referenceId, String endpoint, String endpointId) {
		jdbcTemplate.update("update reference set endpoint = ?, endpointid = ? where id = ?",
				endpoint, endpointId, referenceId);
	}
	
	private void deleteReference(int relationId, String endpoint) {
		jdbcTemplate.update("delete reference where relation_id = ? and endpoint = ?",
				relationId, endpoint);
	}

	private Reference findReferenceByEndpointAndCommonID(String commonID, String endpoint) throws EntityNotFoundException {
		return getEntityById("select * from reference inner join relation on relation.id = reference.relation_id where commonid = ? and endpoint = ?", 
				new Object[] {commonID, endpoint}, new RowMapper<Reference>() {
					public Reference mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						Reference reference = RelationFactory.createRelationReference(
								rs.getInt("reference.id"),
								rs.getString("endpoint"), 
								rs.getString("endpointid"));
						return reference;
					}}, "Could not find Reference with the provided Identifier");

	}
	
	private Relation findRelationByEndpointAndEndpointID(Integer entityTypeId, String endpoint, String endpointId) throws EntityNotFoundException {
		return getEntityById("select * from reference inner join relation on relation.id = reference.relation_id where entitytype_id = ? and endpoint = ? and endpointid = ?", 
				new Object[] {entityTypeId, endpoint, endpointId}, new RowMapper<Relation>() {
			public Relation mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				Relation relation = RelationFactory.createRelation();
				relation.setId(rs.getInt("relation.id"));
				relation.setCommonID(rs.getString("commonid"));
				relation.getReference().addAll(getReferences(rs.getInt("relation.id")));
				return relation;
			}}, "Could not find Relation with the provided Identifiers");
		
	}

	private <T> T getEntityById(String sql, Object[] ids, RowMapper<T> rowMapper, String errorMessage) throws EntityNotFoundException {
		try {
			return jdbcTemplate.queryForObject(sql, ids, rowMapper); 
		} catch(EmptyResultDataAccessException e) {
			throw new EntityNotFoundException(errorMessage);
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public CacheAccessor getCacheAccessor() {
		return cacheAccessor;
	}

	public void setCacheAccessor(CacheAccessor cacheAccessor) {
		this.cacheAccessor = cacheAccessor;
	}
	

}
