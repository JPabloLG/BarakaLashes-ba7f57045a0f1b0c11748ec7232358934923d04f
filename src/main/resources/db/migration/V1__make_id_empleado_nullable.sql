-- Make id_empleado nullable so citas can be created without assigning an employee
ALTER TABLE Cita MODIFY COLUMN id_empleado INT NULL;