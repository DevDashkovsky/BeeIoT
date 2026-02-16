package postgres

import (
	"context"
	"os"
	"testing"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
)

// fake connection implementing PgxConn
type FakeConn struct {
	closed bool
}

func (f *FakeConn) Exec(ctx context.Context, sql string, arguments ...any) (pgconn.CommandTag, error) {
	var tag pgconn.CommandTag
	return tag, nil
}
func (f *FakeConn) Query(ctx context.Context, sql string, args ...any) (pgx.Rows, error) {
	return nil, nil
}
func (f *FakeConn) QueryRow(ctx context.Context, sql string, args ...any) pgx.Row { return nil }
func (f *FakeConn) Close(ctx context.Context) error                               { f.closed = true; return nil }

func TestCreateConnectPath_MissingEnv(t *testing.T) {
	old := map[string]string{
		"DB_USER":     os.Getenv("DB_USER"),
		"DB_PASSWORD": os.Getenv("DB_PASSWORD"),
		"DB_HOST":     os.Getenv("DB_HOST"),
		"DB_PORT":     os.Getenv("DB_PORT"),
		"DB_NAME":     os.Getenv("DB_NAME"),
	}
	defer func() {
		_ = os.Setenv("DB_USER", old["DB_USER"])
		_ = os.Setenv("DB_PASSWORD", old["DB_PASSWORD"])
		_ = os.Setenv("DB_HOST", old["DB_HOST"])
		_ = os.Setenv("DB_PORT", old["DB_PORT"])
		_ = os.Setenv("DB_NAME", old["DB_NAME"])
	}()
	_ = os.Unsetenv("DB_USER")
	_ = os.Unsetenv("DB_PASSWORD")
	_ = os.Unsetenv("DB_HOST")
	_ = os.Unsetenv("DB_PORT")
	_ = os.Unsetenv("DB_NAME")

	db := &Postgres{}
	_, err := db.createConnectPath()
	if err == nil {
		t.Fatalf("expected error when DB env not set")
	}
}

func TestCreateConnectPath_Success(t *testing.T) {
	old := map[string]string{
		"DB_USER":     os.Getenv("DB_USER"),
		"DB_PASSWORD": os.Getenv("DB_PASSWORD"),
		"DB_HOST":     os.Getenv("DB_HOST"),
		"DB_PORT":     os.Getenv("DB_PORT"),
		"DB_NAME":     os.Getenv("DB_NAME"),
	}
	defer func() {
		_ = os.Setenv("DB_USER", old["DB_USER"])
		_ = os.Setenv("DB_PASSWORD", old["DB_PASSWORD"])
		_ = os.Setenv("DB_HOST", old["DB_HOST"])
		_ = os.Setenv("DB_PORT", old["DB_PORT"])
		_ = os.Setenv("DB_NAME", old["DB_NAME"])
	}()
	_ = os.Setenv("DB_USER", "u")
	_ = os.Setenv("DB_PASSWORD", "p")
	_ = os.Setenv("DB_HOST", "h")
	_ = os.Setenv("DB_PORT", "1")
	_ = os.Setenv("DB_NAME", "n")

	db := &Postgres{}
	path, err := db.createConnectPath()
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if path == "" {
		t.Fatalf("expected non-empty path")
	}
}

func TestCloseDB_WithFakeConn(t *testing.T) {
	db := &Postgres{}
	fc := &FakeConn{}
	db.conn = fc
	if err := db.CloseDB(); err != nil {
		t.Fatalf("CloseDB failed: %v", err)
	}
	if !fc.closed {
		t.Fatalf("expected underlying FakeConn to be closed")
	}
}
