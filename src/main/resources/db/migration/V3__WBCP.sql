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

ALTER TABLE app_parameters 
  ADD COLUMN creation_instant_locale timestamp without time zone,
  ADD COLUMN creation_instant_utc timestamp without time zone,
  ADD COLUMN lastupdate_instant_locale timestamp without time zone,
  ADD COLUMN lastupdate_instant_utc timestamp without time zone;

UPDATE app_parameters SET
  creation_instant_locale = base_entities.creation_instant_locale,
  creation_instant_utc = base_entities.creation_instant_utc,
  lastupdate_instant_locale = base_entities.lastupdate_instant_locale,
  lastupdate_instant_utc = base_entities.lastupdate_instant_utc
FROM
  base_entities
WHERE
  base_entities.id = app_parameters.id
;


ALTER TABLE users 
  ADD COLUMN creation_instant_locale timestamp without time zone,
  ADD COLUMN creation_instant_utc timestamp without time zone,
  ADD COLUMN lastupdate_instant_locale timestamp without time zone,
  ADD COLUMN lastupdate_instant_utc timestamp without time zone;

UPDATE users SET
  creation_instant_locale = base_entities.creation_instant_locale,
  creation_instant_utc = base_entities.creation_instant_utc,
  lastupdate_instant_locale = base_entities.lastupdate_instant_locale,
  lastupdate_instant_utc = base_entities.lastupdate_instant_utc
FROM
  base_entities
WHERE
  base_entities.id = users.id
;


ALTER TABLE messages 
  ADD COLUMN creation_instant_locale timestamp without time zone,
  ADD COLUMN creation_instant_utc timestamp without time zone,
  ADD COLUMN lastupdate_instant_locale timestamp without time zone,
  ADD COLUMN lastupdate_instant_utc timestamp without time zone;

UPDATE messages SET
  creation_instant_locale = base_entities.creation_instant_locale,
  creation_instant_utc = base_entities.creation_instant_utc,
  lastupdate_instant_locale = base_entities.lastupdate_instant_locale,
  lastupdate_instant_utc = base_entities.lastupdate_instant_utc
FROM
  base_entities
WHERE
  base_entities.id = messages.id
;


ALTER TABLE organizations 
  ADD COLUMN creation_instant_locale timestamp without time zone,
  ADD COLUMN creation_instant_utc timestamp without time zone,
  ADD COLUMN lastupdate_instant_locale timestamp without time zone,
  ADD COLUMN lastupdate_instant_utc timestamp without time zone;

UPDATE organizations SET
  creation_instant_locale = base_entities.creation_instant_locale,
  creation_instant_utc = base_entities.creation_instant_utc,
  lastupdate_instant_locale = base_entities.lastupdate_instant_locale,
  lastupdate_instant_utc = base_entities.lastupdate_instant_utc
FROM
  base_entities
WHERE
  base_entities.id = organizations.id
;


ALTER TABLE counters 
  ADD COLUMN creation_instant_locale timestamp without time zone,
  ADD COLUMN creation_instant_utc timestamp without time zone,
  ADD COLUMN lastupdate_instant_locale timestamp without time zone,
  ADD COLUMN lastupdate_instant_utc timestamp without time zone;

UPDATE counters SET
  creation_instant_locale = base_entities.creation_instant_locale,
  creation_instant_utc = base_entities.creation_instant_utc,
  lastupdate_instant_locale = base_entities.lastupdate_instant_locale,
  lastupdate_instant_utc = base_entities.lastupdate_instant_utc
FROM
  base_entities
WHERE
  base_entities.id = counters.id
;

DROP TABLE base_entities CASCADE;
