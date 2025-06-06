DROP INDEX IF EXISTS flightinstance_flightdate_ticketcost_idx;
DROP INDEX IF EXISTS flight_planeid_num_idx;
DROP INDEX IF EXISTS planeid_repair_date_idx;


CREATE INDEX flightinstance_flightdate_ticketcost_idx ON flightinstance USING BTREE (flightnumber, flightdate, ticketcost);
CREATE INDEX flight_planeid_num_idx ON flight USING BTREE (flightnumber, planeid);
CREATE INDEX planeid_repair_date_idx ON repair USING BTREE (planeid, repairdate);