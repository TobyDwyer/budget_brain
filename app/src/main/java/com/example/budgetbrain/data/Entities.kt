package com.example.budgetbrain.data

import androidx.room.*
import java.util.Date
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "language_preference") val languagePreference: String = "en",
    @ColumnInfo(name = "savings_goal") val savingsGoal: Double = 0.0,
    @ColumnInfo(name = "sso_key") val ssoKey: String? = null,
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(), // Non-nullable, generate UUID
    @ColumnInfo(name = "user_id") val userId: String, // Foreign key to User
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "start_date") val startDate: Date,
    @ColumnInfo(name = "end_date") val endDate: Date,
    @ColumnInfo(name = "budgeted_amount") val budgetedAmount: Double,
    @ColumnInfo(name = "remaining_amount") val remainingAmount: Double,
    @ColumnInfo(name = "created_at") val createdAt: Date? = Date(),
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false, // Default to false
)

@Entity(
    tableName = "budget_category_cross_ref",
    primaryKeys = ["budgetId", "categoryId"], // Composite primary key
    foreignKeys = [
        ForeignKey(entity = BudgetEntity::class, parentColumns = ["id"], childColumns = ["budgetId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CategoryEntity::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class BudgetCategoryCrossRef(
    @ColumnInfo(name = "budgetId") val budgetId: String,
    @ColumnInfo(name = "categoryId") val categoryId: String
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "name") val name: String
)

data class BudgetWithCategories(
    @Embedded val budget: BudgetEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BudgetCategoryCrossRef::class,
            parentColumn = "budgetId",
            entityColumn = "categoryId"
        ),
        entity = CategoryEntity::class // Specify the entity explicitly
    )
    val categories: List<CategoryEntity>
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "budget_id") val budgetId: String,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Date = Date(),
    @ColumnInfo(name = "is_synced") val isSynced: Boolean = false
)


data class CategoryAmount(
    val categoryName: String,
    val totalTransacted: Double
)