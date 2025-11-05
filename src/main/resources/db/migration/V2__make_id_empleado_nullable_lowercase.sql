-- Ensure id_empleado is nullable on actual table name used by MySQL
ALTER TABLE cita MODIFY COLUMN id_empleado INT NULL;