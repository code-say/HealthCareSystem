package com.cg.healthcare.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.cg.healthcare.entities.DiagnosticCenter;
import com.cg.healthcare.entities.DiagnosticTest;

@Repository
public class IDiagnosticCenterRepositoryIntImpl implements IDiagnosticCenterRepository{
	@PersistenceContext
	EntityManager em;

	@Override
	public DiagnosticCenter getDiagnosticCenter(String centername) {
		TypedQuery<DiagnosticCenter> q=em.createQuery("select s from DiagnosticCenter s where s.name = :name",DiagnosticCenter.class);
		q.setParameter("name", centername);
		return q.getResultList().get(0);	
		}

	@Override
	public List<DiagnosticTest> viewTestDetails(int diagnosticCenterId) {
		TypedQuery<DiagnosticTest> q=em.createQuery("select a from DiagnosticTest a join a.diagnosticCenter d "
				+ " where d.diagonasticCenterid = :id",DiagnosticTest.class);
		q.setParameter("id", diagnosticCenterId);
		return q.getResultList();
	}

}
