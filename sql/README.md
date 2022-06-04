# SQL 

## DDL

``` SQL

CREATE TABLE ers_user_roles(
	role_id varchar NOT NULL,
	role varchar UNIQUE,
	
	CONSTRAINT pk_role_id
		PRIMARY KEY (role_id)
	
);

CREATE TABLE ers_reimbursement_types(
	type_id varchar NOT NULL,
	type varchar UNIQUE,
	
	CONSTRAINT pk_type_id
		PRIMARY KEY (type_id)
	
);

CREATE TABLE ers_reimbursement_statuses(
	status_id varchar NOT NULL,
	status varchar UNIQUE,
	
	CONSTRAINT pk_status_id
		PRIMARY KEY (status_id)
	
);

CREATE TABLE ers_users(
	user_id varchar NOT NULL,
	username varchar NOT NULL UNIQUE,
	email varchar NOT NULL UNIQUE,
	PASSWORD varchar NOT NULL,
	given_name varchar NOT NULL,
	is_active boolean,
	role_id varchar,
	
	CONSTRAINT pk_user_id
		PRIMARY KEY (user_id),
			
	CONSTRAINT fk_role_id
		FOREIGN KEY(role_id)
			REFERENCES ers_user_roles (role_id) ON UPDATE CASCADE ON DELETE CASCADE

);



CREATE TABLE ers_reimbursements (
	reimb_id varchar NOT NULL,
	amount numeric(6,2) NOT NULL,
	submitted timestamp NOT NULL,
	resolved timestamp,
	description varchar NOT NULL,
	receipt bytea,
	payment_id varchar,
	author_id varchar NOT NULL,
	resolver_id varchar,
	status_id varchar NOT NULL,
	type_id varchar NOT NULL,
	
	CONSTRAINT pk_reimbursement_id
		PRIMARY KEY (reimb_id),
			
	CONSTRAINT fk_author_id
		FOREIGN KEY(author_id)
			REFERENCES ers_users (user_id) ON UPDATE CASCADE ON DELETE CASCADE,
			
--	CONSTRAINT fk_resolver_id
--		FOREIGN KEY(resolver_id)
--			REFERENCES ers_users ( NULL ) ON UPDATE CASCADE ON DELETE CASCADE,			

	CONSTRAINT fk_status_id
		FOREIGN KEY(status_id)
			REFERENCES ers_reimbursement_statuses (status_id) ON UPDATE CASCADE ON DELETE CASCADE,
			
	CONSTRAINT fk_type_id
		FOREIGN KEY(type_id)
			REFERENCES ers_reimbursement_types (type_id) ON UPDATE CASCADE ON DELETE CASCADE
			
);
```