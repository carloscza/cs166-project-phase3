-- Customer 1
SELECT s.departuretime, s.arrivaltime, fi.numofstops,ROUND( 100 * AVG( (fi.departedontime AND fi.arrivedontime)::int),1) AS ontimepercentage FROM flightinstance fi JOIN schedule s ON s.flightnumber = fi.flightnumber AND s.dayofweek = TO_CHAR(fi.flightdate, 'FMDay') JOIN flight f ON f.flightnumber = fi.flightnumber WHERE departurecity = 'New York' AND arrivalcity = 'Boston' AND flightdate = '2025-05-05' GROUP BY  s.departuretime, s.arrivaltime, fi.numofstops;

-- Customer 2
SELECT ticketcost FROM flightinstance WHERE flightnumber = 'F102' AND flightdate = '2025-05-05';

-- Customer 3
SELECT p.make, p.model FROM flight f JOIN plane p ON f.planeid = p.planeid WHERE f.flightnumber = 'F101';

-- Tech 1
SELECT repairdate, repaircode FROM repair WHERE planeid = 'PL001' AND repairdate BETWEEN '2025-05-05' AND '2025-05-20' ORDER BY repairdate;

-- Tech 2
SELECT m.requestdate, m.repaircode, p.make, p.model, p.year, p.lastrepairdate FROM maintenancerequest m JOIN plane p ON m.planeid = p.planeid WHERE pilotid = 'P001';




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