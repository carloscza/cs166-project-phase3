DROP INDEX IF EXISTS f_num_date;
DROP INDEX IF EXISTS f_seatsold;
DROP INDEX IF EXISTS c_id;
DROP INDEX IF EXISTS p_id;

CREATE INDEX f_num_date ON FlightInstance USING BTREE (FlightNumber, FlightDate);
CREATE INDEX f_seatsold ON FlightInstance USING BTREE (SeatsSold);
CREATE INDEX c_id ON Customer USING BTREE (CustomerID);
CREATE INDEX p_id ON Plane USING BTREE (PlaneID);

--Management 2
SELECT (SeatsTotal - SeatsSold) AS SeatsAvailable, SeatsSold
FROM Flight JOIN FlightInstance USING (FlightNumber)
WHERE FlightNumber = 'F100' AND FlightDate = '5/5/25';

--Management 3
SELECT *
FROM Flight JOIN FlightInstance USING (FlightNumber)
WHERE FlightNumber = 'F100' AND FlightDate = '5/5/25';

SELECT *
FROM Flight JOIN FlightInstance USING (FlightNumber)
WHERE FlightNumber = 'F100' AND FlightDate = '5/5/25' AND DepartedOnTime;

SELECT *
FROM Flight JOIN FlightInstance USING (FlightNumber)
WHERE FlightNumber = 'F100' AND FlightDate = '5/5/25' AND ArrivedOnTime;

--Management 5
SELECT *
FROM Flight JOIN FlightInstance USING (FlightNumber) JOIN Reservation USING (FlightInstanceID)
WHERE FlightNumber = 'F100' AND FlightDate = '5/5/25';

SELECT (CustomerID) AS ReserevedPassenger
FROM Flight JOIN FlightInstance USING (FlightNumber) JOIN Reservation USING (FlightInstanceID)
WHERE FlightNumber = 'F100' AND FlightDate = '5/5/25' AND Status = 'reserved';

SELECT (CustomerID) AS WaitlistPassenger
FROM Flight JOIN FlightInstance USING (FlightNumber) JOIN Reservation USING (FlightInstanceID)
WHERE FlightNumber = 'F100' AND FlightDate = '5/5/25' AND Status = 'waitlist';

SELECT (CustomerID) AS FlownPassenger
FROM Flight JOIN FlightInstance USING (FlightNumber) JOIN Reservation USING (FlightInstanceID)
WHERE FlightNumber = 'F100' AND FlightDate = '5/5/25' AND Status = 'flown';

--Management 7
SELECT Make, Model, Year, LastRepairDate
FROM Plane
WHERE PlaneID = 'PL001';

--Management 8
SELECT RepairID, PlaneID, RepairCode, RepairDate
FROM Technician JOIN Repair USING (TechnicianID)
WHERE TechnicianID = 'T001';

--Management 9
SELECT RepairDate, RepairCode
FROM Plane JOIN Repair USING (PlaneID)
WHERE PlaneID = 'PL001' AND RepairDate BETWEEN '2025-04-01' AND '2025-04-20';

--Management 10
SELECT COUNT(*) AS FlightDays, SUM(SeatsSold) AS SoldTotal, SUM(SeatsTotal - SeatsSold) AS UnsoldTotal
FROM Flight JOIN FlightInstance USING (FlightNumber)
WHERE FlightNumber = 'F100' AND FlightDate BETWEEN '5/5/25' AND '5/15/15';
        	
