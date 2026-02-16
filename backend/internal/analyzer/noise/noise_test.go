package noise

import (
	"BeeIOT/internal/domain/interfaces"
	"BeeIOT/internal/domain/models/dbTypes"
	"BeeIOT/internal/domain/models/httpType"
	"context"
	"testing"
	"time"

	"github.com/rs/zerolog"
)

type MockDB struct {
	interfaces.DB
	Hives     []dbTypes.Hive
	NoiseData map[time.Time][]dbTypes.HivesNoiseData
	MockEmail string
}

func (m *MockDB) GetHives(ctx context.Context, email string) ([]dbTypes.Hive, error) {
	return m.Hives, nil
}

func (m *MockDB) GetNoiseSinceTimeMap(ctx context.Context, id int, date time.Time) (map[time.Time][]dbTypes.HivesNoiseData, error) {
	return m.NoiseData, nil
}

func (m *MockDB) GetUserById(ctx context.Context, id int) (string, error) {
	return m.MockEmail, nil
}

type MockInMemoryDB struct {
	interfaces.InMemoryDB
	Notifications []httpType.NotificationData
}

func (m *MockInMemoryDB) SetNotification(ctx context.Context, email string, note httpType.NotificationData) error {
	m.Notifications = append(m.Notifications, note)
	return nil
}

func TestAnalyzeNoise(t *testing.T) {
	// Setup context with logger
	logger := zerolog.Nop()
	ctx := context.WithValue(context.Background(), "logger", logger)

	// Setup times
	ct := time.Now()
	testDate := time.Date(ct.Year(), ct.Month(), ct.Day(), 0, 0, 0, 0, time.UTC).Add(-24 * time.Hour)
	prevDate := testDate.Add(-24 * time.Hour)

	// Setup MockDB
	noiseData := make(map[time.Time][]dbTypes.HivesNoiseData)

	// Previous day data (low noise)
	noiseData[prevDate] = []dbTypes.HivesNoiseData{
		{Level: 100}, {Level: 120}, {Level: 110},
	}

	// Current day data (high noise spike)
	noiseData[testDate] = []dbTypes.HivesNoiseData{
		{Level: 500}, {Level: 550}, {Level: 525}, // Avg ~525. Prev avg ~110. Diff ~415 > 200
	}

	mockDB := &MockDB{
		Hives: []dbTypes.Hive{
			{Id: 1, NameHive: "NoiseHive"},
		},
		NoiseData: noiseData,
		MockEmail: "noise@example.com",
	}

	mockInMemDB := &MockInMemoryDB{}

	analyzer := NewAnalyzer(ctx, 1*time.Hour, mockDB, mockInMemDB)

	// Run analysis
	analyzer.analyzeNoise()

	// Check results
	if len(mockInMemDB.Notifications) != 1 {
		t.Errorf("Expected 1 notification, got %d", len(mockInMemDB.Notifications))
	} else {
		note := mockInMemDB.Notifications[0]
		if note.NameHive != "NoiseHive" {
			t.Errorf("Expected hive name NoiseHive, got %s", note.NameHive)
		}
	}
}

func TestAverageNoise(t *testing.T) {
	analyzer := &Analyzer{} // methods are on pointer receiver

	data := []dbTypes.HivesNoiseData{
		{Level: 100},
		{Level: 200},
		{Level: 300},
	}

	avg := analyzer.averageNoise(data)
	if avg != 200.0 {
		t.Errorf("Expected average 200.0, got %f", avg)
	}

	emptyData := []dbTypes.HivesNoiseData{}
	avgEmpty := analyzer.averageNoise(emptyData)
	if avgEmpty != 0.0 {

	}
}
