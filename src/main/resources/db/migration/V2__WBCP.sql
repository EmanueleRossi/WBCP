/*
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
	
CREATE TABLE base_entities (
    id bigint NOT NULL,
    creation_instant_locale timestamp without time zone,
    creation_instant_utc timestamp without time zone,
    lastupdate_instant_locale timestamp without time zone,
    lastupdate_instant_utc timestamp without time zone
);

ALTER TABLE ONLY base_entities
    ADD CONSTRAINT base_entities_pkey PRIMARY KEY (id);	

CREATE TABLE app_parameters (
    description character varying(65535) NOT NULL,
    name character varying(255) NOT NULL,
    parameter_value character varying(65535) NOT NULL,
    locale_tag character varying(255) NOT NULL,
    id bigint NOT NULL
);

ALTER TABLE app_parameters 
    ADD CONSTRAINT app_parameters_pkey PRIMARY KEY (id);	
ALTER TABLE app_parameters
    ADD CONSTRAINT uk_app_parameters_name UNIQUE (name, locale_tag);
ALTER TABLE app_parameters
    ADD CONSTRAINT fk_app_parameters_base_entities FOREIGN KEY (id) REFERENCES base_entities(id);		

CREATE TABLE users (
    account_expiration_instant timestamp without time zone NOT NULL,
    account_start_instant timestamp without time zone NOT NULL,
    email_address character varying(255) NOT NULL,
    first_name character varying(255) NOT NULL,
    last_name character varying(255) NOT NULL,
    password_hash_base64 character varying(255) NOT NULL,
    public_key_base64 character varying(1024) NOT NULL,
    tax_code character varying(255),
    id bigint NOT NULL
);

ALTER TABLE users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
ALTER TABLE users
    ADD CONSTRAINT uk_users_email UNIQUE (email_address);
ALTER TABLE users
    ADD CONSTRAINT fk_users_base_entites FOREIGN KEY (id) REFERENCES base_entities(id);	

CREATE TABLE messages (
    aes_key_rsa_crypted_base64 character varying(1024) NOT NULL,
    instant timestamp without time zone NOT NULL,
    payload text NOT NULL,
    author text NOT NULL,
    id bigint NOT NULL,
    user_id bigint
);

ALTER TABLE messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (id);
ALTER TABLE messages
    ADD CONSTRAINT fk_users_user_id FOREIGN KEY (user_id) REFERENCES users(id);	
ALTER TABLE messages
    ADD CONSTRAINT fk_messages_base_entities FOREIGN KEY (id) REFERENCES base_entities(id);

CREATE TABLE organizations (
    name character varying(255) NOT NULL,
    tax_code character varying(255),
    mail_domain character varying(255),
    user_in_charge_id bigint,
    id bigint NOT NULL
);

ALTER TABLE organizations
    ADD CONSTRAINT organizations_pkey PRIMARY KEY (id);
ALTER TABLE organizations
    ADD CONSTRAINT fk_organizations_base_entities FOREIGN KEY (id) REFERENCES base_entities(id);	
ALTER TABLE organizations
    ADD CONSTRAINT fk_organizations_user_in_charge_id FOREIGN KEY (user_in_charge_id) REFERENCES users(id);	

CREATE TABLE organizations_users (
    organization_id bigint NOT NULL,
    user_id bigint NOT NULL
);

ALTER TABLE organizations_users
    ADD CONSTRAINT organizations_users_pkey PRIMARY KEY (organization_id, user_id);

CREATE TABLE counters (
    year bigint NOT NULL,
    lenght bigint NOT NULL,
    separator character varying(255),
    value bigint NOT NULL,
    organization_id bigint NOT NULL,
    id bigint NOT NULL
);

ALTER TABLE counters
    ADD CONSTRAINT counters_pkey PRIMARY KEY (id);
ALTER TABLE counters
    ADD CONSTRAINT fk_counters_organizations FOREIGN KEY (organization_id) REFERENCES organizations(id);