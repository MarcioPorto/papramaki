class CreateBudgets < ActiveRecord::Migration
  def change
    create_table :budgets do |t|
      t.float :amount
      t.float :balance

      t.timestamps null: false
    end
  end
end
