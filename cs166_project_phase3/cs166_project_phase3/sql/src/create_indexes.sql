DROP INDEX IF EXISTS flightinstance_flightdate_ticketcost_idx;
DROP INDEX IF EXISTS flight_planeid_num_idx;
DROP INDEX IF EXISTS planeid_repair_date_idx;

DROP INDEX IF EXISTS f_num_date;
DROP INDEX IF EXISTS f_seatsold;
DROP INDEX IF EXISTS c_id;
DROP INDEX IF EXISTS p_id;


CREATE INDEX flightinstance_flightdate_ticketcost_idx ON flightinstance USING BTREE (flightnumber, flightdate, ticketcost);
CREATE INDEX flight_planeid_num_idx ON flight USING BTREE (flightnumber, planeid);
CREATE INDEX planeid_repair_date_idx ON repair USING BTREE (planeid, repairdate);

CREATE INDEX f_num_date ON FlightInstance USING BTREE (FlightNumber, FlightDate);
CREATE INDEX f_seatsold ON FlightInstance USING BTREE (SeatsSold);
CREATE INDEX c_id ON Customer USING BTREE (CustomerID);
CREATE INDEX p_id ON Plane USING BTREE (PlaneID);