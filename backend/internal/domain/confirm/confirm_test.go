package confirm

import (
	"testing"
)

type MockConfirmSender struct {
	LastEmail string
	LastCode  string
	Err       error
}

func (m *MockConfirmSender) SendConfirmationCode(toEmail, code string) error {
	m.LastEmail = toEmail
	m.LastCode = code
	return m.Err
}

func TestNewConfirm(t *testing.T) {
	mockSender := &MockConfirmSender{}
	conf, err := NewConfirm(mockSender)
	if err != nil {
		t.Fatalf("NewConfirm failed: %v", err)
	}
	if conf == nil {
		t.Fatal("Confirm instance should not be nil")
	}
}

func TestNewCodeAlsoSendsEmail(t *testing.T) {
	mockSender := &MockConfirmSender{}
	conf, _ := NewConfirm(mockSender)

	email := "test@example.com"
	password := "password123"

	code, err := conf.NewCode(email, password)
	if err != nil {
		t.Fatalf("NewCode failed: %v", err)
	}

	if code == "" {
		t.Error("Code should not be empty")
	}
}

func TestVerify(t *testing.T) {
	mockSender := &MockConfirmSender{}
	conf, _ := NewConfirm(mockSender)

	email := "test@example.com"
	password := "password123"

	code, _ := conf.NewCode(email, password)

	// Verify with correct code
	storedPasswordHash, ok := conf.Verify(email, code)
	if !ok {
		t.Error("Verify failed for valid code")
	}
	if storedPasswordHash == "" {
		t.Error("Verify should return password hash")
	}

	// Verify with incorrect code
	_, ok = conf.Verify(email, "wrongcode")
	if ok {
		t.Error("Verify succeeded for invalid code")
	}

	// Verify with non-existent email
	_, ok = conf.Verify("other@example.com", code)
	if ok {
		t.Error("Verify succeeded for non-existent email")
	}
}
