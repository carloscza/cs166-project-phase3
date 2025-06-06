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