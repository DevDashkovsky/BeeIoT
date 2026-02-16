package postgres

import (
	"BeeIOT/internal/domain/models/dbTypes"
	"BeeIOT/internal/domain/models/httpType"
	"context"
	"testing"
	"time"

	"github.com/jackc/pgx/v5"
	"github.com/pashagolub/pgxmock/v4"
)

// Helper to init mock
func newMock() (pgxmock.PgxConnIface, *Postgres, error) {
	mock, err := pgxmock.NewConn()
	if err != nil {
		return nil, nil, err
	}

	db := &Postgres{
		conn: mock,
	}

	return mock, db, nil
}

func TestRegistration(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected when opening a stub database connection", err)
	}
	defer mock.Close(context.Background())

	reg := httpType.Registration{
		Email:    "test@example.com",
		Password: "hashed_password",
	}

	// Expect Exec
	mock.ExpectExec("INSERT INTO users").
		WithArgs(reg.Email, reg.Password).
		WillReturnResult(pgxmock.NewResult("INSERT", 1))

	if err = db.Registration(context.Background(), reg); err != nil {
		t.Errorf("error was not expected while registering: %s", err)
	}

	if err := mock.ExpectationsWereMet(); err != nil {
		t.Errorf("there were unfulfilled expectations: %s", err)
	}
}

func TestIsExistUser(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	email := "test@example.com"

	// Case 1: User exists
	rows := pgxmock.NewRows([]string{"id"}).AddRow(1)
	mock.ExpectQuery("SELECT id FROM users").WithArgs(email).WillReturnRows(rows)

	exists, err := db.IsExistUser(context.Background(), email)
	if err != nil {
		t.Errorf("error was not expected: %s", err)
	}
	if !exists {
		t.Error("expected user to exist")
	}

	// Case 2: User does not exist
	mock.ExpectQuery("SELECT id FROM users").WithArgs("unknown@example.com").WillReturnError(pgx.ErrNoRows)

	exists, err = db.IsExistUser(context.Background(), "unknown@example.com")
	if err != nil {
		t.Errorf("error was not expected calling IsExistUser with nonexistent: %s", err)
	}
	if exists {
		t.Error("expected user not to exist")
	}

	if err := mock.ExpectationsWereMet(); err != nil {
		t.Errorf("there were unfulfilled expectations: %s", err)
	}
}

func TestLogin(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	login := httpType.Login{Email: "test@example.com"}
	passwordHash := "hash123"

	// Case 1: Success
	rows := pgxmock.NewRows([]string{"password"}).AddRow(passwordHash)
	mock.ExpectQuery("SELECT password FROM users").WithArgs(login.Email).WillReturnRows(rows)

	res, err := db.Login(context.Background(), login)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if res != passwordHash {
		t.Errorf("expected hash %s, got %s", passwordHash, res)
	}

	// Case 2: Not found
	mock.ExpectQuery("SELECT password FROM users").WithArgs("unknown").WillReturnError(pgx.ErrNoRows)

	res, err = db.Login(context.Background(), httpType.Login{Email: "unknown"})
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if res != "" {
		t.Errorf("expected empty string, got %s", res)
	}
}

func TestChangePassword(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	cp := httpType.ChangePassword{Email: "test@test.com", Password: "newhash"}

	mock.ExpectExec("UPDATE users").
		WithArgs(cp.Password, cp.Email).
		WillReturnResult(pgxmock.NewResult("UPDATE", 1))

	if err := db.ChangePassword(context.Background(), cp); err != nil {
		t.Errorf("error not expected: %s", err)
	}
}

func TestDeleteUser(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	email := "delete@test.com"

	mock.ExpectExec("DELETE FROM users").
		WithArgs(email).
		WillReturnResult(pgxmock.NewResult("DELETE", 1))

	if err := db.DeleteUser(context.Background(), email); err != nil {
		t.Errorf("error not expected: %s", err)
	}
}

func TestGetUserById(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	id := 1
	email := "user@test.com"

	rows := pgxmock.NewRows([]string{"email"}).AddRow(email)
	mock.ExpectQuery("SELECT email FROM users").WithArgs(id).WillReturnRows(rows)

	res, err := db.GetUserById(context.Background(), id)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if res != email {
		t.Errorf("expected %s, got %s", email, res)
	}
}

func TestNewHive(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	email := "owner@test.com"
	hiveName := "MyHive"

	// NewHive uses a subquery for user_id, but pgxmock matches SQL string.
	mock.ExpectExec("INSERT INTO hives").
		WithArgs(email, hiveName).
		WillReturnResult(pgxmock.NewResult("INSERT", 1))

	if err := db.NewHive(context.Background(), email, hiveName); err != nil {
		t.Errorf("error not expected: %s", err)
	}
}

func TestGetHives(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	// Case 1: Specific email
	email := "owner@test.com"
	rows := pgxmock.NewRows([]string{"id", "name", "email", "temperature_check", "noise_check"}).
		AddRow(1, "Hive1", email, time.Now(), time.Now())

	mock.ExpectQuery("SELECT id, name, .* FROM hives WHERE user_id").
		WithArgs(email).
		WillReturnRows(rows)

	hives, err := db.GetHives(context.Background(), email)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(hives) != 1 || hives[0].NameHive != "Hive1" {
		t.Error("unexpected hives result")
	}

	// Case 2: All hives (email empty)
	rows2 := pgxmock.NewRows([]string{"id", "name", "email", "temperature_check", "noise_check"}).
		AddRow(1, "Hive1", "user1", time.Now(), time.Now()).
		AddRow(2, "Hive2", "user2", time.Now(), time.Now())

	mock.ExpectQuery("SELECT id, name, .* FROM hives;").
		WillReturnRows(rows2)

	hives2, err := db.GetHives(context.Background(), "")
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(hives2) != 2 {
		t.Error("expected 2 hives")
	}
}

func TestNewTemperature(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	temp := httpType.Temperature{
		Temperature: 25.5,
		Time:        time.Now(),
		Email:       "owner@test.com",
		Hive:        "Hive1",
	}

	// NewTemperature calculates ids via subqueries.
	mock.ExpectExec("INSERT INTO temperature").
		WithArgs(temp.Email, temp.Hive, temp.Temperature, temp.Time).
		WillReturnResult(pgxmock.NewResult("INSERT", 1))

	if err := db.NewTemperature(context.Background(), temp); err != nil {
		t.Errorf("error not expected: %s", err)
	}
}

func TestNewNoise(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	noise := httpType.NoiseLevel{
		Level: 50.0,
		Time:  time.Now(),
		Email: "owner@test.com",
		Hive:  "Hive1",
	}

	mock.ExpectExec("INSERT INTO noise").
		WithArgs(noise.Email, noise.Hive, noise.Level, noise.Time).
		WillReturnResult(pgxmock.NewResult("INSERT", 1))

	if err := db.NewNoise(context.Background(), noise); err != nil {
		t.Errorf("error not expected: %s", err)
	}
}

func TestGetEmailHiveBySensorID(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	sensorID := "s1"
	email := "owner@test.com"
	hive := "Hive1"

	rows := pgxmock.NewRows([]string{"email", "name"}).AddRow(email, hive)
	mock.ExpectQuery("SELECT u.email, h.name FROM users u").
		WithArgs(sensorID).
		WillReturnRows(rows)

	resEmail, resHive, err := db.GetEmailHiveBySensorID(context.Background(), sensorID)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if resEmail != email || resHive != hive {
		t.Errorf("unexpected result: %s, %s", resEmail, resHive)
	}
}

func TestDeleteHive(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	email := "owner@test.com"
	hiveName := "MyHive"

	mock.ExpectExec("DELETE FROM hives").
		WithArgs(email, hiveName).
		WillReturnResult(pgxmock.NewResult("DELETE", 1))

	if err := db.DeleteHive(context.Background(), email, hiveName); err != nil {
		t.Errorf("error not expected: %s", err)
	}
}

func TestGetHiveByName(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	email := "owner@test.com"
	hiveName := "MyHive"
	rows := pgxmock.NewRows([]string{"id", "name", "email", "temperature_check", "noise_check"}).
		AddRow(1, hiveName, email, time.Now(), time.Now())

	mock.ExpectQuery("SELECT id, name, .* FROM hives WHERE user_id").
		WithArgs(email, hiveName).
		WillReturnRows(rows)

	res, err := db.GetHiveByName(context.Background(), email, hiveName)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if res.NameHive != hiveName {
		t.Errorf("expected hive name %s, got %s", hiveName, res.NameHive)
	}
}

func TestUpdateHive(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	oldName := "OldHive"
	newName := "NewHive"
	hive := dbTypes.Hive{
		NameHive: newName,
		Email:    "owner@test.com",
	}

	mock.ExpectExec("UPDATE hives").
		WithArgs(hive.NameHive, hive.Email, oldName).
		WillReturnResult(pgxmock.NewResult("UPDATE", 1))

	if err := db.UpdateHive(context.Background(), oldName, hive); err != nil {
		t.Errorf("error not expected: %s", err)
	}
}

func TestDeleteNoise(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	noise := httpType.NoiseLevel{
		Level: 50.0,
		Time:  time.Now(),
		Email: "owner@test.com",
		Hive:  "Hive1",
	}

	mock.ExpectExec("DELETE FROM noise").
		WithArgs(noise.Email, noise.Hive, noise.Time).
		WillReturnResult(pgxmock.NewResult("DELETE", 1))

	if err := db.DeleteNoise(context.Background(), noise); err != nil {
		t.Errorf("error not expected: %s", err)
	}
}

func TestGetNoiseSinceTime(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	email := "owner@test.com"
	hiveName := "Hive1"
	since := time.Now().Add(-1 * time.Hour)

	rows := pgxmock.NewRows([]string{"level", "recorded_at"}).
		AddRow(50.0, time.Now())

	mock.ExpectQuery("SELECT level, recorded_at FROM noise").
		WithArgs(email, hiveName, since).
		WillReturnRows(rows)

	res, err := db.GetNoiseSinceTime(context.Background(), email, hiveName, since)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(res) != 1 {
		t.Errorf("expected 1 result, got %d", len(res))
	}
}

func TestGetNoiseSinceTimeMap(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	id := 1
	date := time.Now().Add(-24 * time.Hour)

	rows := pgxmock.NewRows([]string{"level", "recorded_at"}).
		AddRow(50.0, time.Now())

	mock.ExpectQuery("SELECT level, recorded_at FROM noise").
		WithArgs(id, date).
		WillReturnRows(rows)

	res, err := db.GetNoiseSinceTimeMap(context.Background(), id, date)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(res) == 0 {
		t.Error("expected non-empty map")
	}
}

func TestDeleteTemperature(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	temp := httpType.Temperature{
		Temperature: 25.0,
		Time:        time.Now(),
		Email:       "owner@test.com",
		Hive:        "Hive1",
	}

	mock.ExpectExec("DELETE FROM temperature").
		WithArgs(temp.Email, temp.Hive, temp.Time).
		WillReturnResult(pgxmock.NewResult("DELETE", 1))

	if err := db.DeleteTemperature(context.Background(), temp); err != nil {
		t.Errorf("error not expected: %s", err)
	}
}

func TestGetTemperaturesSinceTime(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	email := "owner@test.com"
	hiveName := "Hive1"
	hive := dbTypes.Hive{
		NameHive: hiveName,
		Email:    email,
	}
	since := time.Now().Add(-1 * time.Hour)

	rows := pgxmock.NewRows([]string{"level", "recorded_at"}).
		AddRow(25.0, time.Now())

	mock.ExpectQuery("SELECT level, recorded_at FROM temperature").
		WithArgs(email, hiveName, since).
		WillReturnRows(rows)

	res, err := db.GetTemperaturesSinceTime(context.Background(), hive, since)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(res) != 1 {
		t.Errorf("expected 1 result, got %d", len(res))
	}
}

func TestGetTasksByUserID(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	task := httpType.Task{
		Email: "user@test.com",
		Hash:  "pass123",
	}

	rows := pgxmock.NewRows([]string{"task_id", "name", "time", "email", "name"}).
		AddRow(1, "Task1", int64(time.Now().UnixMicro()), "user@test.com", "Hive1")

	mock.ExpectQuery("SELECT t.task_id, nt.name, t.time, u.email, h.name FROM tasks t").
		WithArgs(task.Email, task.Hash).
		WillReturnRows(rows)

	res, err := db.GetTasksByUserID(context.Background(), task)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(res) != 1 {
		t.Errorf("expected 1 task, got %d", len(res))
	}
}

func TestGetTasksByHiveID(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	task := httpType.Task{
		Email: "user@test.com",
		Hash:  "pass123",
		Hive:  "Hive1",
	}

	rows := pgxmock.NewRows([]string{"task_id", "name", "time", "email", "name"}).
		AddRow(1, "Task1", int64(time.Now().UnixMicro()), "user@test.com", "Hive1")

	mock.ExpectQuery("SELECT t.task_id, nt.name, t.time, u.email, h.name FROM tasks t").
		WithArgs(task.Hive, task.Email, task.Hash).
		WillReturnRows(rows)

	res, err := db.GetTasksByHiveID(context.Background(), task)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(res) != 1 {
		t.Errorf("expected 1 task, got %d", len(res))
	}
}

func TestGetTaskForDay(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	task := httpType.Task{
		Email: "user@test.com",
		Hash:  "pass123",
		Hive:  "Hive1",
	}

	rows := pgxmock.NewRows([]string{"task_id", "name", "time", "email", "name"}).
		AddRow(1, "Task1", int64(time.Now().UnixMicro()), task.Email, task.Hive)

	mock.ExpectQuery("SELECT t.task_id, nt.name, t.time, u.email, h.name FROM tasks t").
		WithArgs(task.Hive, task.Email, task.Hash, pgxmock.AnyArg()).
		WillReturnRows(rows)

	res, err := db.GetTaskForDay(context.Background(), task)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(res) != 1 {
		t.Errorf("expected 1 task, got %d", len(res))
	}
}

func TestGetTaskForWeek(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	task := httpType.Task{
		Email: "user@test.com",
		Hash:  "pass123",
		Hive:  "Hive1",
	}

	rows := pgxmock.NewRows([]string{"task_id", "name", "time", "email", "name"}).
		AddRow(1, "Task1", int64(time.Now().UnixMicro()), task.Email, task.Hive)

	mock.ExpectQuery("SELECT t.task_id, nt.name").
		WithArgs(task.Hive, task.Email, task.Hash, pgxmock.AnyArg()).
		WillReturnRows(rows)

	res, err := db.GetTaskForWeek(context.Background(), task)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(res) != 1 {
		t.Errorf("expected 1 task, got %d", len(res))
	}
}

func TestGetTaskForMonth(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	task := httpType.Task{
		Email: "user@test.com",
		Hash:  "pass123",
		Hive:  "Hive1",
	}

	rows := pgxmock.NewRows([]string{"task_id", "name", "time", "email", "name"}).
		AddRow(1, "Task1", int64(time.Now().UnixMicro()), task.Email, task.Hive)

	mock.ExpectQuery("SELECT t.task_id, nt.name").
		WithArgs(task.Hive, task.Email, task.Hash, pgxmock.AnyArg()).
		WillReturnRows(rows)

	res, err := db.GetTaskForMonth(context.Background(), task)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(res) != 1 {
		t.Errorf("expected 1 task, got %d", len(res))
	}
}

func TestGetHiveWeightForDay(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	hive := httpType.Hive{
		Email:    "user@test.com",
		Hash:     "pass123",
		NameHive: "Hive1",
	}

	rows := pgxmock.NewRows([]string{"weight", "time"}).
		AddRow(50.0, time.Now())

	mock.ExpectQuery("SELECT weight, time FROM weight_hive").
		WithArgs(hive.Email, hive.Hash, hive.NameHive, pgxmock.AnyArg()).
		WillReturnRows(rows)

	res, err := db.GetHiveWeightForDay(context.Background(), hive)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(res) != 1 {
		t.Errorf("expected 1 weight record, got %d", len(res))
	}
}

func TestGetHiveWeightForWeek(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	hive := httpType.Hive{
		Email:    "user@test.com",
		Hash:     "pass123",
		NameHive: "Hive1",
	}

	rows := pgxmock.NewRows([]string{"weight", "time"}).
		AddRow(50.0, time.Now())

	mock.ExpectQuery("SELECT weight, time FROM weight_hive").
		WithArgs(hive.Email, hive.Hash, hive.NameHive, pgxmock.AnyArg()).
		WillReturnRows(rows)

	res, err := db.GetHiveWeightForWeek(context.Background(), hive)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(res) != 1 {
		t.Errorf("expected 1 weight record, got %d", len(res))
	}
}

func TestGetHiveWeightForMonth(t *testing.T) {
	mock, db, err := newMock()
	if err != nil {
		t.Fatalf("an error '%s' was not expected", err)
	}
	defer mock.Close(context.Background())

	hive := httpType.Hive{
		Email:    "user@test.com",
		Hash:     "pass123",
		NameHive: "Hive1",
	}

	rows := pgxmock.NewRows([]string{"weight", "time"}).
		AddRow(50.0, time.Now())

	mock.ExpectQuery("SELECT weight, time FROM weight_hive").
		WithArgs(hive.Email, hive.Hash, hive.NameHive, pgxmock.AnyArg()).
		WillReturnRows(rows)

	res, err := db.GetHiveWeightForMonth(context.Background(), hive)
	if err != nil {
		t.Errorf("error not expected: %s", err)
	}
	if len(res) != 1 {
		t.Errorf("expected 1 weight record, got %d", len(res))
	}
}
