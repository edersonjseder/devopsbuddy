package com.devopsbuddy.backend.service;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.enums.PlansEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by root on 15/06/17.
 */
@Service
@Transactional(readOnly = true)
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    /**
     * Returns the plan for the given id or null if none is found
     *
     * @param planId the plan id
     * @return The Plan object for the given id or null if it couldn't find one
     */
    public Plan findPlanById(int planId) {
        return planRepository.findOne(planId);
    }

    /**
     * It creates a Basic or a Pro plan
     * @param planId The plan id
     * @return the created plan
     * @throws IllegalArgumentException If the plan is not 1 or 2
     */
    @Transactional // We annotated it because this method will change database state
    public Plan createPlan(int planId) {

        Plan plan = null;

        if (planId == 1){
            plan = planRepository.save(new Plan(PlansEnum.BASIC));

        } else if (planId == 2){
            plan = planRepository.save(new Plan(PlansEnum.PRO));

        } else {
            throw new IllegalArgumentException("Plan id " + planId + " not recognized.");

        }

        return plan;
    }
}
