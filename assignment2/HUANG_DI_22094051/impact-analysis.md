# Assignment 2 – Impact Analysis

## 1. Addressed Component

**Component Selected:**  
`CustomerServiceImpl.saveCustomer(Customer customer, boolean doValidate, NameConflictStrategy nameConflictStrategy)`

**File Path:**  
`dao/src/main/java/org/thingsboard/server/dao/customer/CustomerServiceImpl.java`

**Rationale:**  
The `saveCustomer` method is a core orchestration point in the customer management workflow.  
It coordinates validation, name conflict resolution, persistence, cache eviction, and domain event publishing.  
Due to its multiple conditional branches and interactions with several services, it is an ideal candidate for impact analysis.

---

## 2. Impact Analysis Graph & Completeness

**Graph Type:** Call Graph

The Call Graph models the execution flow of `saveCustomer` and highlights how changes to this method may impact dependent components.

### Covered Execution Paths:
- **Pre-save logic:**  
  - Retrieval of existing customer data (`customerDao.findById`) for update scenarios  
  - Name conflict resolution via `uniquifyEntityName`  
  - Conditional validation using `customerValidator.validate`
- **Core persistence operation:**  
  - Database write through `customerDao.saveAndFlush`
- **Post-save side effects:**  
  - Dashboard updates for non-public customers  
  - Entity count eviction for newly created customers  
  - Cache eviction and domain event publication (`SaveEntityEvent`)
- **Exception handling path:**  
  - Cache rollback via `handleEvictEvent`  
  - Constraint violation detection through `checkConstraintViolation`

### Completeness Justification:
The graph includes all major method calls and conditional branches that influence system behavior.  
Low-impact operations such as logging statements and simple getters/setters are intentionally omitted, as they do not affect control flow or system dependencies.  
Both normal execution and exception handling paths are explicitly represented to ensure a complete impact analysis.

---

## 3. Impact and Maintenance Insights

- Changes to the validation or name conflict policy logic may affect downstream persistence and event publishing behavior.
- Modifications to the persistence layer (`saveAndFlush`) can have cascading impacts on dashboards, cache consistency, and entity count tracking.
- The tight coupling between persistence and post-save side effects suggests that regression testing should include both database state verification and event-driven behavior.
- When maintaining this method, special attention should be given to exception handling to prevent cache inconsistencies.
